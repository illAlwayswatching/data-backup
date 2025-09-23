import { deletes, get, getBaseURL, post } from './request'

const api = {
  // 是否登录成功
  getLoginStatus: ({username, password}) => post(`user/login`, {username, password}),
  // 注册
  register: ({username, password}) => post(`user/register`, {username, password}),
  // 获取目录项
  getInfo: (username, path) => get(`info/?username=${username}&path=${path}`),
  // 新建文件夹
  buildFoler: (folderName, username, path) => get(`info/addFolder?folderName=${folderName}&username=${username}&target=${path}`),
  // 删除文件夹
  deleteFolder: (folderId) => deletes(`info/${folderId}`),
  // 上传文件
  uploadEncryptedFile: () => `${getBaseURL()}upload/fileEncrypt`,
  uploadEncryptedFileSerpent: () => `${getBaseURL()}upload/fileEncryptSerpent`,
  uploadEncryptedFileTwofish: () => `${getBaseURL()}upload/fileEncryptTwofish`,
  uploadEncryptedFileCamellia: () => `${getBaseURL()}upload/fileEncryptCamellia`,
  uploadEncryptedFileChacha20: () => `${getBaseURL()}upload/fileEncryptChacha20`,
  uploadFile: () => `${getBaseURL()}upload/file`,
  // 压缩
  compress: (username, target, zipName, sourceIds) => post(`compress/byId`, {username, target, zipName, sourceIds}),
  // 解压
  decompress: (username, target, zipId) => post(`decompress/byId`, {username, target, zipId}),
  // 移动文件
  moveFile: (username, to, fileId) => post(`info/copyInServer`, {username, to, fileId}),
  // 下载文件
  downloadFile: (username, source) => `${getBaseURL()}download/file?username=${username}&source=${source}`,
  downloadEncryptedFile: (username, source, keyword) => `${getBaseURL()}download/fileDecrypt?username=${username}&source=${source}&keyword=${keyword}`
}

export { api }
