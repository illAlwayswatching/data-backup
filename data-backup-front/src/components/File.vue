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
                  @contextmenu="onFileMenu($event, item, deleteFolder, compress, decompress, moveFile, downloadFile)"
                  @dblclick="enterFolder(item)"
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

  let path = '/'
  let pathList = []
  let searchKey = ref('')
  let centerDialogVisible = ref(false)
  let isEncrypted = ref(false)
  
  let loadParams = ref({username: counterStore.account.replace(/\"/g, ""), target: path})
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
    ElMessageBox.prompt('请输入文件夹名', '新建文件夹', {
      confirmButtonText: 'OK',
      cancelButtonText: 'Cancel',
    })
    .then(async ({ value }) => {
      const res = (await api.buildFoler(value, counterStore.account.replace(/\"/g, ""), path))

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
          download(true, source, value)
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
      download(false, source, '')
    }
  }

  const compress = async (fileId) => {
    ElMessageBox.prompt('请输入压缩之后的文件名', '压缩', {
      confirmButtonText: 'OK',
      cancelButtonText: 'Cancel',
    })
    .then(async ({ value }) => {
      const res = (await api.compress(counterStore.account.replace(/\"/g, ""), path, value, fileId))

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

  getInfo()

  async function decompress(fileId) {
    const res = (await api.decompress(counterStore.account.replace(/\"/g, ""), path, fileId))

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

    const result = (await api.getInfo(counterStore.account.replace(/\"/g, ""), path))
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
      pathList.push(path)
      path = item.path + '/'
      console.log(pathList)
      getInfo()
    }
  }

  function goBack() {
    if (path != '/') {
      path = pathList.pop()
      console.log(pathList)
      console.log(path)
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
</style>
