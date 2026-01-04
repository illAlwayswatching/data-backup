<template>
  <div class="container">
    <div class="main-layout">
      <!-- 左侧操作栏 -->
      <div class="left-sidebar">
        <el-button class="action-btn" type="primary" @click="goBack">返回上一级</el-button>
        <el-button class="action-btn" type="primary" @click="addFile">上传</el-button>
        <el-button class="action-btn" type="primary" @click="buildFoler">新建文件夹</el-button>
      </div>

      <!-- 右侧内容区 -->
      <div class="right-content">
        <!-- 搜索框 -->
        <div class="search-box">
          <el-input
            clearable
            v-model="searchKey"
            placeholder="搜索文件"
            :prefix-icon="Search"
            @keyup.enter="search"
            @blur="search"
          />
        </div>

        <!-- 文件列表 -->
        <div class="file-list-container">
          <el-scrollbar height="calc(100vh - 200px)">
            <el-row>
              <el-col
                v-model="fileInfo"
                v-for="(item, k) in fileInfo" :key="item.id"
                :span="3" :offset="k % 6? 1 : 0"
                class="custom-col"
              >
                <el-card 
                  :body-style="{ padding: '10px', boxShadow: 'none'}"  
                  :class="{ 
                    'draggable-file': item.type !== 1, 
                    'drop-target': item.type === 1 && dragOverTarget === item.id,
                    'dragging': draggedItem?.id === item.id
                  }"
                  :draggable="item.type !== 1"
                  @contextmenu="onFileMenu($event, item, deleteFolder, compress, decompress, moveFile, downloadFile, clearFolder)"
                  @dblclick="enterFolder(item)"
                  @dragstart="handleDragStart(item, $event)"
                  @dragend="handleDragEnd"
                  @dragover.prevent="handleDragOver(item, $event)"
                  @dragenter.prevent="handleDragEnter(item, $event)"
                  @dragleave="handleDragLeave(item, $event)"
                  @drop.prevent="handleDrop(item, $event)"
                >
                  <div class="image-wrapper">
                    <img v-if="item.isCompressed" src="@/assets/images/zip.jpeg" class="image">
                    <img v-else-if="item.type === 1" src="@/assets/images/folder.png" class="image">
                    <img v-else-if="item.type == 2" src="@/assets/images/file.png" class="image">
                    <img v-else src="@/assets/images/img.png" class="image">
                  </div>
                  <el-scrollbar height="20px" style="padding: 5px">
                    <span>{{item.name}}</span>
                  </el-scrollbar>
                </el-card>
              </el-col>
            </el-row>
          </el-scrollbar>
        </div>
      </div>
    </div>

    <el-dialog title="上传文件" v-model="centerDialogVisible">
      <div class="dialog">
        <el-upload
          :data="loadParams"
          drag
          :action="uploadFile()"
          multiple
          :on-success="handleSuccess"
        >
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            Drop file here or <em>click to upload</em>
          </div>
        </el-upload>
      </div>
    </el-dialog>
  </div>
</template>

<script setup name="File">
  import { Search, UploadFilled } from '@element-plus/icons-vue'
  import { h,ref } from 'vue'
  import { onFileMenu } from '@/utils/menu'
  import { api } from '@/utils/api'
  import { useCounterStore } from '@/stores/counter'
  import { ElButton, ElMessage, ElMessageBox,ElOption,ElSelect } from 'element-plus'
  import "element-plus/theme-chalk/el-loading.css";
  import "element-plus/theme-chalk/el-message.css";
  import "element-plus/theme-chalk/el-notification.css";
  import "element-plus/theme-chalk/el-message-box.css";
  import "element-plus/theme-chalk/el-drawer.css";


  const counterStore = useCounterStore()

  let path = ref('/')
  let pathList = []
  let searchKey = ref('')
  let centerDialogVisible = ref(false)
  let isEncrypted = ref(false)
  
  // 拖拽相关状态
  let dragOverTarget = ref(null) // 当前拖拽悬停的目标文件夹ID
  let draggedItem = ref(null) // 当前被拖拽的文件信息
  
  let loadParams = ref({username: counterStore.account.replace(/\"/g, ""), target: path.value})
  
  // 监听 path 变化，更新 loadParams
  import { watch } from 'vue'
  watch(path, (newPath) => {
    loadParams.value.target = newPath
  })
  let fileInfo = ref([])
  let tempFileInfo = ref([])
  let algorithm = ref('AES')

  const createChoseAlgorithmBox = () => {
    ElMessageBox({
          title: '请选择加密算法',
          message: h('div', [
            h(ElSelect, {
              modelValue: algorithm.value,
              placeholder: '请选择加密算法',
              style: 'width: 100%;',
              // onChange: (value) => {
              //   algorithm.value = value
              // },
              'onUpdate:modelValue': (value) => {
                algorithm.value = value
                console.log('选择的算法:', value) // 添加日志查看是否触发
                ElMessageBox.close()
                setTimeout(() => {
                  createChoseAlgorithmBox () // 递归调用，重新打开
                }, 100) // 短暂延迟确保关闭完成
      }
            }, [
              h(ElOption, { label: 'AES-128', value: 'AES' }),
              h(ElOption, { label: 'Serpent', value: 'Serpent' }),
              h(ElOption, { label: 'Twofish', value: 'Twofish' }),
              h(ElOption, { label: 'Camellia', value: 'Camellia' }),
              h(ElOption, { label: 'Chacha20', value: 'Chacha20' }),
            ])
          ]),
          showCancelButton: true,
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          closeOnClickModal: false,
          closeOnPressEscape: false,
        }).then(() => {
          ElMessage({
            type: 'info',
            message: `已选择${algorithm.value}算法`,
          })

          ElMessageBox.prompt(`已选择${algorithm.value}算法,请输入16位数字密钥`, 'Tip', {
            confirmButtonText: 'OK',
            cancelButtonText: 'Cancel',
            inputPattern: /^\d{16}$/,
            inputErrorMessage: 'Invalid Keyword',
          })
          .then(({ value }) => {
            isEncrypted.value = true
            loadParams.value.keyword = value
            centerDialogVisible.value = true
          })
          .catch(() => {
            ElMessage({
              type: 'info',
              message: '操作取消',
            })

        }).catch(() => {
          ElMessage({
            type: 'info',
            message: '操作取消',
          })
        })
        console.log('加密')
      })

  }
  
  const addFile = async() => {
      ElMessageBox.confirm(
      '请问是否加密文件?',
      '上传文件',
      {
        confirmButtonText: 'Yes',
        cancelButtonText: 'No',
        type: 'warning',
        distinguishCancelAndClose: true,
      })
      .then(() => {
        createChoseAlgorithmBox()
  }).catch((action) => {
        if (action === 'cancel'){
          console.log('不加密')
          isEncrypted.value = false
          centerDialogVisible.value = true

        }else if (action === 'close'){
          ElMessage({
            type: 'info',
            message: '操作取消',
          })
        }
      })
}

const buildFoler = async () => {
    // 确保路径格式正确：根目录为 '/'，其他目录以 '/' 结尾
    let currentPath = path.value || '/'
    // 确保路径以 '/' 开头
    if (!currentPath.startsWith('/')) {
      currentPath = '/' + currentPath
    }
    // 如果不是根目录，确保以 '/' 结尾
    if (currentPath !== '/' && !currentPath.endsWith('/')) {
      currentPath = currentPath + '/'
    }
    console.log('当前路径:', currentPath)
    
    ElMessageBox.prompt('请输入文件夹名', '新建文件夹', {
      confirmButtonText: 'OK',
      cancelButtonText: 'Cancel',
    })
    .then(async ({ value }) => {
      if (!value || value.trim() === '') {
        ElMessage.error('文件夹名不能为空')
        return
      }
      
      const res = (await api.buildFoler(value.trim(), counterStore.account.replace(/\"/g, ""), currentPath))

      if (res.type === 'success') {
        ElMessage({
          type: 'success',
          message: res.message,
        })

        getInfo()
      } else {
        ElMessage.error(res.message)
      }
    })
    .catch(() => {
      ElMessage("操作取消")
    })
  }
  
const downloadFile = (source, flag) => {
    if (flag) {
      ElMessageBox.confirm(
        '请问是否解密文件?',
        '下载文件',
        {
          confirmButtonText: 'OK',
          cancelButtonText: 'Cancel',
          type: 'warning',
        }
      )
      .then(() => {
        ElMessageBox.prompt('请输入16位数字密钥', 'Tip', {
          confirmButtonText: 'OK',
          cancelButtonText: 'Cancel',
          inputPattern: /^\d{16}$/,
          inputErrorMessage: 'Invalid Keyword',
        })
        .then(({ value }) => {
          // 询问用户选择下载方式
          ElMessageBox.confirm(
            '请选择下载方式',
            '下载文件',
            {
              confirmButtonText: '指定位置',
              cancelButtonText: '默认下载',
              distinguishCancelAndClose: true,
              type: 'info',
            }
          )
          .then(() => {
            // 用户选择指定位置下载
            downloadToLocation(true, source, value)
          })
          .catch((action) => {
            if (action === 'cancel') {
              // 用户选择默认下载
              download(true, source, value)
            }
          })
        })
        .catch(() => {
          ElMessage({
            type: 'info',
            message: '操作取消',
          })
        })
      })
      .catch(() => {
        download(false, source, '')
      })
    } else {
      // 询问用户选择下载方式
      ElMessageBox.confirm(
        '请选择下载方式',
        '下载文件',
        {
          confirmButtonText: '指定位置',
          cancelButtonText: '默认下载',
          distinguishCancelAndClose: true,
          type: 'info',
        }
      )
      .then(() => {
        // 用户选择指定位置下载
        downloadToLocation(false, source, '')
      })
      .catch((action) => {
        if (action === 'cancel') {
          // 用户选择默认下载
          download(false, source, '')
        }
      })
    }
  }

  const compress = async (fileId) => {
    ElMessageBox.prompt('请输入压缩之后的文件名', '压缩', {
      confirmButtonText: 'OK',
      cancelButtonText: 'Cancel',
    })
    .then(async ({ value }) => {
      const res = (await api.compress(counterStore.account.replace(/\"/g, ""), path.value, value, fileId))

      if (res.type === 'success') {
        ElMessage({
          type: 'success',
          message: res.message,
        })

        getInfo()
      } else {
        ElMessage.error(res.message)
      }
    })
    .catch(() => {
      ElMessage("操作取消")
    })
  }

  const deleteFolder = async (folderId) => {
    ElMessageBox.confirm(
      '确定删除此文件 or 文件夹吗？',
      'Warning',
      {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        type: 'warning',
      }
    )
    .then(async () => {
      const res = (await api.deleteFolder(folderId))
      if (res.type === 'success') {
        ElMessage({
          type: 'success',
          message: '删除完成',
        })

        getInfo()
      } else {
        ElMessage.error(res.message)
      }
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '操作取消',
      })
    })
  }

  const clearFolder = async (folderId) => {
    ElMessageBox.confirm(
      '确定要清空此文件夹下的所有文件吗？此操作不可恢复！',
      '清空文件夹',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        distinguishCancelAndClose: true,
      }
    )
    .then(async () => {
      const res = await api.clearFolder(folderId)
      if (res.type === 'success') {
        ElMessage({
          type: 'success',
          message: res.message || '文件夹已清空',
        })
        getInfo()
      } else {
        ElMessage.error(res.message || '清空文件夹失败')
      }
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '操作取消',
      })
    })
  }

  const moveFile = async (fileId) => {
    ElMessageBox.prompt('请输入目标目录', '移动文件', {
      confirmButtonText: 'OK',
      cancelButtonText: 'Cancel',
    })
    .then(async ({ value }) => {
      const res = (await api.moveFile(counterStore.account.replace(/\"/g, ""), value, fileId))

      if (res.type === 'success') {
        ElMessage({
          type: 'success',
          message: res.message,
        })

        getInfo()
      } else {
        ElMessage.error(res.message)
      }
    })
    .catch(() => {
      ElMessage("操作取消")
    })
  }

  // 拖拽开始
  const handleDragStart = (item, event) => {
    // 只能拖拽文件，不能拖拽文件夹
    if (item.type === 1) {
      event.preventDefault()
      return false
    }
    
    draggedItem.value = item
    event.dataTransfer.effectAllowed = 'move'
    event.dataTransfer.setData('application/json', JSON.stringify({
      id: item.id,
      name: item.name,
      type: item.type,
      path: item.path
    }))
    
    // 设置拖拽图标
    event.dataTransfer.setData('text/plain', item.name)
  }

  // 拖拽结束
  const handleDragEnd = () => {
    draggedItem.value = null
    dragOverTarget.value = null
  }

  // 拖拽悬停
  const handleDragOver = (item, event) => {
    // 只有文件夹可以作为放置目标
    if (item.type === 1 && draggedItem.value && draggedItem.value.id !== item.id) {
      event.dataTransfer.dropEffect = 'move'
    } else {
      event.dataTransfer.dropEffect = 'none'
    }
  }

  // 拖拽进入
  const handleDragEnter = (item, event) => {
    // 只有文件夹可以作为放置目标，且不能拖拽到自身
    if (item.type === 1 && draggedItem.value && draggedItem.value.id !== item.id) {
      dragOverTarget.value = item.id
      event.dataTransfer.dropEffect = 'move'
    } else {
      event.dataTransfer.dropEffect = 'none'
    }
  }

  // 拖拽离开
  const handleDragLeave = (item, event) => {
    // 检查是否真的离开了元素（避免子元素触发）
    const rect = event.currentTarget.getBoundingClientRect()
    const x = event.clientX
    const y = event.clientY
    
    if (x < rect.left || x > rect.right || y < rect.top || y > rect.bottom) {
      if (dragOverTarget.value === item.id) {
        dragOverTarget.value = null
      }
    }
  }

  // 放置文件
  const handleDrop = async (item, event) => {
    // 重置拖拽状态
    dragOverTarget.value = null
    
    // 只有文件夹可以作为放置目标
    if (item.type !== 1) {
      ElMessage.warning('只能将文件拖拽到文件夹中')
      return
    }

    // 检查是否拖拽到自身
    if (draggedItem.value && draggedItem.value.id === item.id) {
      ElMessage.warning('不能将文件移动到自身')
      return
    }

    // 获取拖拽的文件信息
    let draggedFile = null
    try {
      const data = event.dataTransfer.getData('application/json')
      if (data) {
        draggedFile = JSON.parse(data)
      } else if (draggedItem.value) {
        draggedFile = draggedItem.value
      }
    } catch (e) {
      console.error('解析拖拽数据失败:', e)
      if (draggedItem.value) {
        draggedFile = draggedItem.value
      }
    }

    if (!draggedFile) {
      ElMessage.error('无法获取拖拽的文件信息')
      return
    }

    // 检查是否是文件夹
    if (draggedFile.type === 1) {
      ElMessage.warning('不能拖拽文件夹')
      return
    }

    // 获取目标文件夹路径
    // item.path 格式可能是: /folder/subfolder (相对于当前目录)
    // 后端API需要: /folder/subfolder/ (相对于用户目录，以 / 结尾)
    let targetPath = item.path || '/'
    
    // 如果路径包含用户名前缀（/username/...），需要去掉
    const username = counterStore.account.replace(/\"/g, "")
    if (targetPath.startsWith('/' + username)) {
      targetPath = targetPath.substring(username.length + 1)
    }
    
    // 确保路径格式正确
    if (!targetPath.startsWith('/')) {
      targetPath = '/' + targetPath
    }
    if (targetPath !== '/' && !targetPath.endsWith('/')) {
      targetPath = targetPath + '/'
    }

    // 检查是否移动到相同位置
    const currentPath = path.value || '/'
    const draggedPath = draggedFile.path || ''
    
    // 简单检查：如果目标路径和当前路径相同，可能是移动到相同位置
    // 这里需要更精确的判断，但为了简化，我们先执行移动操作
    // 后端会处理同名文件的情况

    // 确认移动
    try {
      ElMessage.info(`正在移动文件到 ${item.name}...`)
      
      const res = await api.moveFile(
        counterStore.account.replace(/\"/g, ""), 
        targetPath, 
        draggedFile.id
      )

      if (res.type === 'success') {
        ElMessage({
          type: 'success',
          message: `文件已移动到 ${item.name}`,
        })
        getInfo()
      } else {
        ElMessage.error(res.message || '移动文件失败')
      }
    } catch (error) {
      console.error('移动文件失败:', error)
      ElMessage.error('移动文件失败: ' + (error.message || '未知错误'))
    } finally {
      draggedItem.value = null
    }
  }

  getInfo()

  async function decompress(fileId) {
    const res = (await api.decompress(counterStore.account.replace(/\"/g, ""), path.value, fileId))

    if (res.type === 'success') {
      ElMessage({
        type: 'success',
        message: res.message,
      })
      getInfo()
    } else {
      ElMessage.error(res.message)
    }
  }

  async function getInfo() {
    fileInfo.value = []
    tempFileInfo.value = []

    const result = (await api.getInfo(counterStore.account.replace(/\"/g, ""), path.value))
    fileInfo.value = result.data
    tempFileInfo.value = result.data
  }

  function search() {
    if (!searchKey.value) {
      fileInfo.value = tempFileInfo.value
      return
    }

    let temp = []
    for (let i=0; i < fileInfo.value.length; i++){
      if (fileInfo.value[i].name.includes(searchKey.value)) {
        temp.push(fileInfo.value[i])
      }
    }

    fileInfo.value = temp
  }

  function uploadFile() {
    if (isEncrypted.value === true) {
      if(algorithm.value === 'AES') {
        return api.uploadEncryptedFile()
      } else if (algorithm.value === 'Serpent') {
        return api.uploadEncryptedFileSerpent()
      } else if (algorithm.value === 'Twofish') {
        return api.uploadEncryptedFileTwofish()
      } else if (algorithm.value === 'Camellia') {
        return api.uploadEncryptedFileCamellia()
      } else if (algorithm.value === 'Chacha20') {
        return api.uploadEncryptedFileChacha20()
      }
    } else {
      return api.uploadFile()
    }
  }

  function handleSuccess(response) {
    if (response.type === 'success') {
      ElMessage({
        message: '上传成功',
        type: 'success',
      })

      getInfo()
    } else {
      ElMessage.error(response.message)
    }

    isEncrypted.value = false
  }

  function enterFolder(item) {
    if (item.type === 1) {
      pathList.push(path.value)
      path.value = item.path + '/'
      console.log('进入文件夹，当前路径:', path.value)
      getInfo()
    }
  }

  function goBack() {
    if (path.value != '/') {
      path.value = pathList.pop()
      console.log('返回上一级，当前路径:', path.value)
      getInfo()
    }
  }

  function download(flag, source, keyword) {
    let a = document.createElement('a')
    if (flag) {
      a.href = api.downloadEncryptedFile(counterStore.account.replace(/\"/g, ""), source, keyword)
    } else {
      a.href = api.downloadFile(counterStore.account.replace(/\"/g, ""), source)
    }
    //在新窗口打开
    a.target = '_blank'
    a.click(); 
  }

  // 下载到指定位置
  async function downloadToLocation(flag, source, keyword) {
    // 检查浏览器是否支持 File System Access API
    if (!window.showSaveFilePicker) {
      ElMessage.warning('您的浏览器不支持指定下载位置功能，将使用默认下载方式')
      download(flag, source, keyword)
      return
    }

    try {
      // 从 source 路径中提取文件名
      // source 格式可能是: /folder/file.txt 或 /file.txt
      let fileName = 'download'
      if (source) {
        const pathParts = source.split('/').filter(part => part.length > 0)
        if (pathParts.length > 0) {
          fileName = pathParts[pathParts.length - 1]
        }
      }
      
      // 构建下载 URL
      let downloadUrl
      if (flag) {
        downloadUrl = api.downloadEncryptedFile(counterStore.account.replace(/\"/g, ""), source, keyword)
      } else {
        downloadUrl = api.downloadFile(counterStore.account.replace(/\"/g, ""), source)
      }
      
      // 确保 URL 格式正确（api.js 中已经构建了完整 URL）
      console.log('下载 URL:', downloadUrl)

      // 根据文件扩展名获取 MIME 类型
      const getMimeType = (filename) => {
        const ext = filename.split('.').pop()?.toLowerCase()
        const mimeTypes = {
          'txt': 'text/plain',
          'pdf': 'application/pdf',
          'doc': 'application/msword',
          'docx': 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
          'xls': 'application/vnd.ms-excel',
          'xlsx': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
          'jpg': 'image/jpeg',
          'jpeg': 'image/jpeg',
          'png': 'image/png',
          'gif': 'image/gif',
          'zip': 'application/zip',
          'rar': 'application/x-rar-compressed',
          'mp4': 'video/mp4',
          'mp3': 'audio/mpeg',
          'json': 'application/json',
          'xml': 'application/xml',
          'html': 'text/html',
          'css': 'text/css',
          'js': 'application/javascript',
          'cpp': 'text/x-c++src',
          'java': 'text/x-java-source',
          'py': 'text/x-python'
        }
        return mimeTypes[ext] || null
      }

      // 关键改动：先获取文件数据，再打开文件选择器
      // 这样可以避免在文件选择器打开后浏览器的安全上下文变化导致的问题
      ElMessage.info('正在下载文件...')
      console.log('开始下载，URL:', downloadUrl)
      
      // 先获取文件数据（在用户交互上下文中）
      let blob = null
      try {
        // 尝试使用 fetch
        const response = await fetch(downloadUrl, {
          method: 'GET',
          credentials: 'include', // 包含 cookies
          mode: 'cors'
        })
        
        console.log('响应状态:', response.status, response.statusText)
        console.log('响应类型:', response.type)
        
        if (!response.ok) {
          const errorText = await response.text().catch(() => '')
          throw new Error(`下载失败: ${response.status} ${response.statusText}${errorText ? ' - ' + errorText.substring(0, 100) : ''}`)
        }

        blob = await response.blob()
        console.log('Fetch 成功，Blob 大小:', blob.size, 'bytes', '类型:', blob.type)
      } catch (fetchError) {
        console.warn('Fetch 失败，尝试使用 XMLHttpRequest:', fetchError)
        
        // 如果 fetch 失败，尝试使用 XMLHttpRequest
        blob = await new Promise((resolve, reject) => {
          const xhr = new XMLHttpRequest()
          xhr.open('GET', downloadUrl, true)
          xhr.responseType = 'blob'
          xhr.withCredentials = true
          
          xhr.onload = function() {
            if (xhr.status >= 200 && xhr.status < 300) {
              const blob = xhr.response
              if (!blob || blob.size === 0) {
                reject(new Error('下载失败: 响应数据为空'))
              } else {
                resolve(blob)
              }
            } else {
              reject(new Error(`下载失败: ${xhr.status} ${xhr.statusText}`))
            }
          }
          
          xhr.onerror = function() {
            reject(new Error('网络错误: 无法连接到服务器'))
          }
          
          xhr.ontimeout = function() {
            reject(new Error('下载超时'))
          }
          
          xhr.timeout = 60000
          xhr.send()
        })
        
        console.log('XMLHttpRequest 成功，Blob 大小:', blob.size, 'bytes')
      }
      
      if (!blob || blob.size === 0) {
        throw new Error('下载失败: 响应数据为空')
      }

      // 数据获取成功后再打开文件选择器
      ElMessage.info('请选择保存位置...')
      
      // 构建文件选择器配置
      const pickerOptions = {
        suggestedName: fileName
      }

      // 如果文件名有扩展名且能找到对应的 MIME 类型，则设置 types
      // 否则不设置 types，让浏览器允许保存任何类型的文件
      if (fileName.includes('.')) {
        const mimeType = getMimeType(fileName)
        if (mimeType) {
          const ext = '.' + fileName.split('.').pop()
          pickerOptions.types = [{
            description: '文件',
            accept: {
              [mimeType]: [ext]
            }
          }]
        }
      }

      // 显示保存文件对话框（必须在用户交互上下文中）
      const fileHandle = await window.showSaveFilePicker(pickerOptions)

      // 写入数据到文件
      const writable = await fileHandle.createWritable()
      await writable.write(blob)
      await writable.close()
      
      ElMessage.success('文件下载成功！')
    } catch (error) {
      if (error.name === 'AbortError') {
        // 用户取消了文件选择，不显示错误消息
        return
      } else {
        console.error('下载失败:', error)
        const errorMsg = error.response?.data?.message || error.message || '未知错误'
        ElMessage.error('下载失败: ' + errorMsg)
        // 如果指定位置下载失败，回退到默认下载
        download(flag, source, keyword)
      }
    }
  }
</script>

<style scoped>
  .container {
    width: 100%;
    height: 100%;
    padding: 10px;
    box-sizing: border-box;
  }

  .main-layout {
    display: flex;
    gap: 20px;
    height: 100%;
  }

  /* 左侧操作栏 */
  .left-sidebar {
    width: 180px;
    display: flex;
    flex-direction: column;
    gap: 12px;
    padding: 15px;
    background: rgba(255, 248, 240, 0.7);
    backdrop-filter: blur(15px);
    border-radius: 20px;
    border: 1px solid rgba(184, 184, 230, 0.3);
    box-shadow: 
      0 8px 25px rgba(184, 184, 230, 0.2),
      inset 0 1px 0 rgba(255, 255, 255, 0.5);
    height: fit-content;
    position: relative;
    overflow: hidden;
    align-items: stretch;
  }

  /* 柔光高光 */
  .left-sidebar::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle, rgba(255, 248, 240, 0.2) 0%, transparent 70%);
    pointer-events: none;
  }

  .action-btn {
    width: 100%;
    height: 45px;
    font-size: 15px;
    font-weight: 500;
    letter-spacing: 0.5px;
    border-radius: 15px;
    transition: all 0.3s ease;
    margin: 0;
    padding: 0;
    box-sizing: border-box;
  }

  .action-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(184, 184, 230, 0.4) !important;
  }

  /* 右侧内容区 */
  .right-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 15px;
    min-width: 0;
  }

  /* 搜索框 */
  .search-box {
    padding: 0;
  }
  
  .image-wrapper {
    width: 100%;
    padding-bottom: 100%;
    position: relative;
    overflow: hidden;
    border-radius: 12px;
    margin-bottom: 8px;
  }

  .image {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
    border-radius: 12px;
    filter: brightness(1.05) contrast(0.95) saturate(0.9);
    transition: all 0.3s ease;
  }
  
  .custom-col {
    margin-bottom: 25px;
    padding: 0 8px;
  }
  
  /* 马卡龙色卡片样式增强 */
  .custom-col :deep(.el-card) {
    background: rgba(255, 248, 240, 0.85) !important;
    border: 1px solid rgba(184, 184, 230, 0.3) !important;
    border-radius: 25px !important;
    box-shadow: 
      0 6px 20px rgba(184, 184, 230, 0.25),
      0 2px 8px rgba(212, 197, 232, 0.15),
      inset 0 1px 0 rgba(255, 255, 255, 0.6) !important;
    backdrop-filter: blur(12px) !important;
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1) !important;
    overflow: hidden;
    position: relative;
  }
  
  /* 卡片柔光高光 */
  .custom-col :deep(.el-card::before) {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle, rgba(255, 248, 240, 0.3) 0%, transparent 70%);
    pointer-events: none;
    opacity: 0;
    transition: opacity 0.4s ease;
  }
  
  .custom-col :deep(.el-card:hover::before) {
    opacity: 1;
  }
  
  /* 复古纹理 */
  .custom-col :deep(.el-card::after) {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-image: 
      repeating-linear-gradient(0deg, transparent, transparent 1px, rgba(0, 0, 0, 0.008) 1px, rgba(0, 0, 0, 0.008) 2px),
      repeating-linear-gradient(90deg, transparent, transparent 1px, rgba(0, 0, 0, 0.008) 1px, rgba(0, 0, 0, 0.008) 2px);
    pointer-events: none;
    opacity: 0.3;
    mix-blend-mode: overlay;
  }
  
  .custom-col :deep(.el-card:hover) {
    transform: translateY(-8px) scale(1.02) !important;
    box-shadow: 
      0 12px 35px rgba(184, 184, 230, 0.35),
      0 4px 15px rgba(212, 197, 232, 0.25),
      inset 0 1px 0 rgba(255, 255, 255, 0.7) !important;
    border-color: rgba(184, 184, 230, 0.5) !important;
  }
  
  .custom-col :deep(.el-card:hover .image-wrapper .image) {
    transform: scale(1.05);
    filter: brightness(1.1) contrast(0.98) saturate(0.95);
  }
  
  .custom-col :deep(.el-card__body) {
    padding: 15px !important;
  }
  
  .custom-col :deep(.el-card span) {
    color: #1a2332 !important;
    font-weight: 500;
    text-shadow: 0 1px 2px rgba(255, 255, 255, 0.6);
    font-size: 13px;
    line-height: 1.4;
  }
  
  .search-box :deep(.el-input__wrapper) {
    background-color: rgba(255, 248, 240, 0.9) !important;
    border: 1px solid rgba(184, 184, 230, 0.4) !important;
    border-radius: 15px !important;
    box-shadow: 
      0 4px 15px rgba(184, 184, 230, 0.15),
      inset 0 1px 2px rgba(255, 255, 255, 0.5) !important;
    backdrop-filter: blur(10px) !important;
    transition: all 0.3s ease !important;
  }

  .search-box :deep(.el-input__wrapper:hover) {
    border-color: rgba(184, 184, 230, 0.7) !important;
    box-shadow: 
      0 6px 20px rgba(184, 184, 230, 0.25),
      inset 0 1px 2px rgba(255, 255, 255, 0.6) !important;
  }

  .search-box :deep(.el-input__wrapper.is-focus) {
    border-color: rgba(184, 184, 230, 0.8) !important;
    box-shadow: 
      0 8px 25px rgba(184, 184, 230, 0.3),
      inset 0 1px 2px rgba(255, 255, 255, 0.7) !important;
  }

  .search-box :deep(.el-input__inner) {
    color: #1a2332 !important;
    font-weight: 400;
  }

  .search-box :deep(.el-input__inner::placeholder) {
    color: rgba(26, 35, 50, 0.5) !important;
  }

  /* 文件列表容器 */
  .file-list-container {
    flex: 1;
    min-height: 0;
    padding: 10px;
    background: rgba(255, 248, 240, 0.5);
    backdrop-filter: blur(10px);
    border-radius: 20px;
    border: 1px solid rgba(184, 184, 230, 0.3);
  }

  .dialog {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
    padding: 20px;
  }
  
  /* 滚动条样式 */
  :deep(.el-scrollbar__bar) {
    opacity: 0.6;
  }
  
  :deep(.el-scrollbar__thumb) {
    background: rgba(184, 184, 230, 0.4) !important;
    border-radius: 10px !important;
  }
  
  /* 上传区域样式增强 */
  :deep(.el-upload) {
    width: 100%;
  }
  
  :deep(.el-upload-dragger) {
    background-color: rgba(255, 248, 240, 0.9) !important;
    border: 2px dashed rgba(184, 184, 230, 0.5) !important;
    border-radius: 20px !important;
    backdrop-filter: blur(10px) !important;
    transition: all 0.3s ease !important;
  }
  
  :deep(.el-upload-dragger:hover) {
    border-color: rgba(184, 184, 230, 0.8) !important;
    background-color: rgba(255, 248, 240, 0.95) !important;
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(184, 184, 230, 0.2) !important;
  }
  
  :deep(.el-icon--upload) {
    color: var(--lavender-blue) !important;
    filter: drop-shadow(0 2px 4px rgba(184, 184, 230, 0.3));
  }
  
  :deep(.el-upload__text) {
    color: #1a2332 !important;
  }
  
  :deep(.el-upload__text em) {
    color: #1a2332 !important;
    font-weight: 500;
  }

  /* 拖拽相关样式 */
  .draggable-file {
    cursor: grab !important;
  }

  .draggable-file:active {
    cursor: grabbing !important;
  }

  .dragging {
    opacity: 0.5 !important;
    transform: scale(0.95) !important;
  }

  .drop-target {
    border-color: rgba(103, 126, 255, 0.8) !important;
    background: rgba(103, 126, 255, 0.15) !important;
    box-shadow: 
      0 8px 30px rgba(103, 126, 255, 0.4),
      0 4px 15px rgba(103, 126, 255, 0.3),
      inset 0 1px 0 rgba(255, 255, 255, 0.7) !important;
    transform: scale(1.05) !important;
  }

  .drop-target::before {
    content: '放置文件';
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    background: rgba(103, 126, 255, 0.9);
    color: white;
    padding: 8px 16px;
    border-radius: 12px;
    font-size: 14px;
    font-weight: 600;
    z-index: 10;
    pointer-events: none;
    box-shadow: 0 4px 12px rgba(103, 126, 255, 0.5);
  }

  /* 拖拽时的视觉反馈 */
  .custom-col :deep(.el-card.draggable-file:hover) {
    cursor: grab;
  }

  .custom-col :deep(.el-card.draggable-file:active) {
    cursor: grabbing;
  }

  /* 文件夹作为放置目标时的样式 */
  .custom-col :deep(.el-card.drop-target) {
    animation: pulse 1s ease-in-out infinite;
  }

  @keyframes pulse {
    0%, 100% {
      box-shadow: 
        0 8px 30px rgba(103, 126, 255, 0.4),
        0 4px 15px rgba(103, 126, 255, 0.3),
        inset 0 1px 0 rgba(255, 255, 255, 0.7);
    }
    50% {
      box-shadow: 
        0 12px 40px rgba(103, 126, 255, 0.6),
        0 6px 20px rgba(103, 126, 255, 0.5),
        inset 0 1px 0 rgba(255, 255, 255, 0.8);
    }
  }
</style>

