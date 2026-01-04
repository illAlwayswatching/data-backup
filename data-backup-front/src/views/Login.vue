<template>
  <div class="login-container">
    <div class="title">Data-Backup</div>
    <div class="login">
      <el-form :model="ruleForm" :rules="rules">
        <el-form-item prop="username">
          <el-input v-model="ruleForm.username" placeholder="username"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input type="password" placeholder="password" v-model="ruleForm.password" @keyup.enter="submitForm"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button class="login-btn" type="primary" @click="submitForm">登录</el-button>
        </el-form-item>
        <el-form-item>
          <el-button class="register-btn" type="primary" @click="register">注册</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup name="Login">
  import { reactive } from "vue";
  import { useRouter } from 'vue-router'
  import { api } from '@/utils/api'
  import { ElMessage } from 'element-plus'
  import { useCounterStore } from '@/stores/counter'

  const router = useRouter()
  const counterStore = useCounterStore()

  const ruleForm = reactive({
    username: "admin",
    password: "123456",
  })

  const rules = reactive({
    username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
    password: [{ required: true, message: "请输入密码", trigger: "blur" }],
  })

  async function register() {
    let username = ruleForm.username
    let password = ruleForm.password

    const result = (await api.register({username,password}))

    if (result.type === 'success') {
      ElMessage({
        message: result.message,
        type: 'success',
      })
    } else {
      ElMessage.error(result.message)
    }
  }
  
  async function submitForm() {
    let username = ruleForm.username
    let password = ruleForm.password

    const result = (await api.getLoginStatus({username,password}))

    if (result.type === 'success') {
      ElMessage({
        message: result.message,
        type: 'success',
      })
      
      counterStore.setAccount(username)
      router.push('/Home')
    } else {
      ElMessage.error(result.message)
    }
  }
</script>
  
<style scoped>
  .login-container {
    position: relative;
    width: 100%;
    height: 100%;
    overflow: hidden;
  }
  
  /* 粉橙渐变云效果 - 上层云 */
  .login-container::before {
    content: '';
    position: absolute;
    top: -20%;
    left: -10%;
    width: 120%;
    height: 60%;
    background: radial-gradient(ellipse at 30% 40%, 
      rgba(255, 212, 196, 0.6) 0%, 
      rgba(255, 184, 140, 0.4) 30%, 
      rgba(255, 248, 240, 0.3) 50%, 
      transparent 70%);
    border-radius: 50%;
    filter: blur(60px);
    animation: cloudFloat1 20s ease-in-out infinite;
    z-index: 1;
  }
  
  /* 粉橙渐变云效果 - 中层云 */
  .login-container::after {
    content: '';
    position: absolute;
    top: 10%;
    right: -15%;
    width: 100%;
    height: 50%;
    background: radial-gradient(ellipse at 70% 50%, 
      rgba(255, 248, 240, 0.5) 0%, 
      rgba(255, 184, 140, 0.4) 25%, 
      rgba(255, 212, 196, 0.3) 45%, 
      transparent 65%);
    border-radius: 50%;
    filter: blur(80px);
    animation: cloudFloat2 25s ease-in-out infinite;
    z-index: 1;
  }
  
  /* 云朵浮动动画 */
  @keyframes cloudFloat1 {
    0%, 100% {
      transform: translate(0, 0) scale(1);
      opacity: 0.7;
    }
    50% {
      transform: translate(30px, -20px) scale(1.1);
      opacity: 0.9;
    }
  }
  
  @keyframes cloudFloat2 {
    0%, 100% {
      transform: translate(0, 0) scale(1);
      opacity: 0.6;
    }
    50% {
      transform: translate(-40px, 15px) scale(1.15);
      opacity: 0.8;
    }
  }
  
  /* 背景图片 + 渐变叠加 + 草地层 */
  .login-container {
    background-image: 
      /* 草地层 */
      linear-gradient(180deg, 
        transparent 0%, 
        transparent 75%, 
        rgba(184, 230, 184, 0.3) 80%, 
        rgba(230, 212, 184, 0.4) 90%, 
        rgba(230, 212, 184, 0.5) 100%),
      /* 背景图片 */
      url("../assets/images/background.jpg"),
      /* 主背景渐变叠加 */
      linear-gradient(180deg, 
        rgba(212, 197, 232, 0.7) 0%, 
        rgba(184, 184, 230, 0.6) 30%, 
        rgba(168, 197, 232, 0.6) 60%, 
        rgba(184, 230, 184, 0.5) 85%, 
        rgba(230, 212, 184, 0.5) 100%);
    background-size: 100% 100%, cover, 100% 100%;
    background-position: center, center, center;
    background-attachment: fixed, fixed, fixed;
    background-blend-mode: normal, overlay, normal;
  }
  
  .title {
    position: absolute;
    top: 50%;
    width: 100%;
    margin-top: -100px;
    text-align: center;
    font-size: 42px;
    font-weight: 600;
    color: #ffffff;
    text-shadow: 
      0 2px 8px rgba(0, 0, 0, 0.3),
      0 4px 15px rgba(0, 0, 0, 0.2),
      0 0 20px rgba(0, 0, 0, 0.15);
    letter-spacing: 3px;
    z-index: 10;
    filter: drop-shadow(0 2px 6px rgba(0, 0, 0, 0.25));
  }
  
  .login {
    position: absolute;
    left: 50%;
    top: 50%;
    width: 360px;
    margin: -50px 0 0 -200px;
    padding: 50px 40px;
    border-radius: 30px;
    background: rgba(255, 248, 240, 0.9);
    backdrop-filter: blur(20px);
    box-shadow: 
      0 20px 60px rgba(184, 184, 230, 0.3),
      0 0 0 1px rgba(184, 184, 230, 0.2),
      inset 0 1px 0 rgba(255, 255, 255, 0.5);
    z-index: 10;
    position: relative;
    overflow: hidden;
  }
  
  /* 登录卡片柔光高光 */
  .login::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle, rgba(255, 248, 240, 0.3) 0%, transparent 70%);
    pointer-events: none;
    animation: shimmer 4s ease-in-out infinite;
  }
  
  @keyframes shimmer {
    0%, 100% {
      transform: translate(0, 0) rotate(0deg);
      opacity: 0.3;
    }
    50% {
      transform: translate(20px, 20px) rotate(5deg);
      opacity: 0.5;
    }
  }
  
  /* 复古纹理效果 */
  .login::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-image: 
      repeating-linear-gradient(0deg, transparent, transparent 1px, rgba(0, 0, 0, 0.01) 1px, rgba(0, 0, 0, 0.01) 2px),
      repeating-linear-gradient(90deg, transparent, transparent 1px, rgba(0, 0, 0, 0.01) 1px, rgba(0, 0, 0, 0.01) 2px);
    pointer-events: none;
    opacity: 0.4;
    mix-blend-mode: overlay;
  }
  
  .login-btn,
  .register-btn {
    width: 100%;
    height: 45px;
    font-size: 16px;
    font-weight: 500;
    letter-spacing: 1px;
    border-radius: 15px;
    transition: all 0.3s ease;
    margin: 0;
  }
  
  .login-btn {
    margin-bottom: 12px;
  }
  
  .register-btn {
    background: linear-gradient(135deg, rgba(212, 197, 232, 0.9), rgba(184, 184, 230, 0.9)) !important;
  }
  
  .login-btn:hover,
  .register-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(184, 184, 230, 0.4) !important;
  }
  
  /* 表单项间距调整 */
  .login :deep(.el-form-item) {
    margin-bottom: 20px;
  }
  
  .login :deep(.el-form-item:last-child) {
    margin-bottom: 0;
  }
  
  /* 表单输入框样式增强 */
  .login :deep(.el-input__wrapper) {
    background-color: rgba(255, 248, 240, 0.8) !important;
    border: 1px solid rgba(184, 184, 230, 0.4) !important;
    border-radius: 15px !important;
    box-shadow: 
      0 4px 15px rgba(184, 184, 230, 0.15),
      inset 0 1px 2px rgba(255, 255, 255, 0.5) !important;
    backdrop-filter: blur(10px) !important;
    transition: all 0.3s ease !important;
  }
  
  .login :deep(.el-input__wrapper:hover) {
    border-color: rgba(184, 184, 230, 0.7) !important;
    box-shadow: 
      0 6px 20px rgba(184, 184, 230, 0.25),
      inset 0 1px 2px rgba(255, 255, 255, 0.6) !important;
  }
  
  .login :deep(.el-input__wrapper.is-focus) {
    border-color: rgba(184, 184, 230, 0.8) !important;
    box-shadow: 
      0 8px 25px rgba(184, 184, 230, 0.3),
      inset 0 1px 2px rgba(255, 255, 255, 0.7) !important;
  }
  
  .login :deep(.el-input__inner) {
    color: #1a2332 !important;
    font-weight: 400;
  }
  
  .login :deep(.el-input__inner::placeholder) {
    color: rgba(26, 35, 50, 0.5) !important;
  }
  
  .login :deep(.el-form-item__label) {
    color: #1a2332 !important;
    font-weight: 500;
  }
</style>
