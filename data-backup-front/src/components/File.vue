<template>
  <div class="container">
    <div class="handle-box">
      <el-button class="goBackButton" type="primary" @click="goBack">返回上一级</el-button>
      <el-input
        clearable
        v-model="searchKey"
        placeholder="搜索文件"
        :prefix-icon="Search"
        @keyup.enter="search"
        @blur="search"
      />

      <el-button class="upload" type="primary" @click="addFile">上传</el-button>
      <el-button class="upload" type="primary" @click="buildFoler">新建文件夹</el-button>
    </div>

    <el-scrollbar height="560px">
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
            <img v-if="item.isCompressed" src="@/assets/images/zip.png" class="image">
            <img v-else-if="item.type === 1" src="@/assets/images/folder.png" class="image">
            <img v-else-if="item.type == 2" src="@/assets/images/file.png" class="image">
            <img v-else src="@/assets/images/img.png" class="image">
            <el-scrollbar height="20px" style="padding: 5px">
              <span>{{item.name}}</span>
            </el-scrollbar>
          </el-card>
        </el-col>
      </el-row>
    </el-scrollbar>

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

<style  scoped>
  .handle-box {
    margin-bottom: 10px;
    font-size: 12px;
    display: flex;
    width: 35%;
  }
  .handle-box input{
    margin: 0 10px;
  }
  .image {
    width: 100%;
    height: 100px; 
    object-fit: cover;
    display: block;
  }
  .custom-col {
    margin-bottom: 20px; /* 设置底部间距 */
  }
  .upload {
    margin-left: 10px;
  }
  .goBackButton {
    margin-right: 10px;
  }
  .dialog {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
  }
</style>
