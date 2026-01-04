<template>
  <div class="header">
    <div class="logo">Data-Backup</div>

    <div class="header-right">
      <div class="header-user-con">
        <!-- 用户头像 -->
        <div class="user-avator">
          <img src="@/assets/images/user.jpeg"/>
        </div>

        <!-- 用户名下拉菜单 -->
        <el-dropdown class="user-name" trigger="click" @command="handleCommand">
          <span class="el-dropdown-link">
            {{username}}
            <el-icon><CaretBottom /></el-icon>
          </span>
          
          <template #dropdown>
            <el-dropdown-menu>
              <a href="https://github.com/abel-chai/data-backup.git" target="_blank">
                <el-dropdown-item>项目仓库</el-dropdown-item>
              </a>
  
              <el-dropdown-item divided command="loginOut">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<script setup name="Top">
  import { computed, ref } from 'vue'
  import { useRouter } from 'vue-router'
  import { useCounterStore } from '@/stores/counter'
  import { CaretBottom } from '@element-plus/icons-vue'
  

  let name = ref("admin")
  const router = useRouter()
  const counterStore = useCounterStore()
  
  let username = computed(() => {
    let username = counterStore.account? counterStore.account.replace(/\"/g, ""): null
    return username? username.substring().substring(): name.value.substring()
  })
  
  function handleCommand(commond) {
    if (commond == 'loginOut') {
      counterStore.logOut()
      router.push('/')
    }
  }
</script>
  
<style scoped>
  .header {
    position: relative;
    box-sizing: border-box;
    width: 100%;
    height: 75px;
    font-size: 22px;
    color: #1a2332;
    background: linear-gradient(135deg, 
      rgba(255, 212, 196, 0.75) 0%, 
      rgba(255, 184, 140, 0.8) 30%,
      rgba(212, 197, 232, 0.85) 60%,
      rgba(184, 184, 230, 0.9) 100%);
    backdrop-filter: blur(20px);
    box-shadow: 
      0 4px 20px rgba(255, 184, 140, 0.25),
      0 2px 10px rgba(212, 197, 232, 0.2),
      0 0 0 1px rgba(255, 184, 140, 0.3),
      inset 0 1px 0 rgba(255, 248, 240, 0.4);
    z-index: 1000;
    overflow: hidden;
    border-bottom: 2px solid rgba(255, 184, 140, 0.2);
  }
  
  /* 柔光高光效果 - 粉橙色调 */
  .header::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle, 
      rgba(255, 248, 240, 0.2) 0%, 
      rgba(255, 212, 196, 0.15) 30%,
      transparent 70%);
    pointer-events: none;
    animation: headerShimmer 6s ease-in-out infinite;
  }
  
  @keyframes headerShimmer {
    0%, 100% {
      transform: translate(0, 0) rotate(0deg);
      opacity: 0.15;
    }
    50% {
      transform: translate(30px, 30px) rotate(3deg);
      opacity: 0.25;
    }
  }
  
  /* 复古纹理 */
  .header::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-image: 
      repeating-linear-gradient(0deg, transparent, transparent 1px, rgba(0, 0, 0, 0.015) 1px, rgba(0, 0, 0, 0.015) 2px),
      repeating-linear-gradient(90deg, transparent, transparent 1px, rgba(0, 0, 0, 0.015) 1px, rgba(0, 0, 0, 0.015) 2px);
    pointer-events: none;
    opacity: 0.3;
    mix-blend-mode: overlay;
  }
  
  .header .logo {
    float: left;
    width: 280px;
    line-height: 75px;
    padding-left: 40px;
    font-weight: 600;
    font-size: 26px;
    letter-spacing: 2px;
    color: #1a2332;
    text-shadow: 
      0 2px 4px rgba(255, 255, 255, 0.9),
      0 0 10px rgba(255, 248, 240, 0.6),
      0 1px 2px rgba(255, 184, 140, 0.3);
    position: relative;
    z-index: 1;
    filter: drop-shadow(0 2px 4px rgba(255, 255, 255, 0.7));
    transition: all 0.3s ease;
  }
  
  .header .logo:hover {
    text-shadow: 
      0 2px 6px rgba(255, 255, 255, 1),
      0 0 15px rgba(255, 248, 240, 0.7),
      0 2px 3px rgba(255, 184, 140, 0.4);
    transform: translateX(2px);
  }
  
  .header-right {
    float: right;
    padding-right: 50px;
    position: relative;
    z-index: 1;
  }
  
  .header-user-con {
    display: flex;
    height: 75px;
    align-items: center;
    gap: 15px;
  }
  
  .user-name {
    margin-left: 10px;
  }
  
  .user-avator {
    margin-left: 20px;
    position: relative;
    transition: all 0.3s ease;
  }
  
  .user-avator::before {
    content: '';
    position: absolute;
    top: -3px;
    left: -3px;
    width: calc(100% + 6px);
    height: calc(100% + 6px);
    border-radius: 50%;
    background: linear-gradient(135deg, 
      rgba(255, 248, 240, 0.4), 
      rgba(184, 184, 230, 0.3));
    box-shadow: 
      0 0 0 2px rgba(184, 184, 230, 0.4),
      0 4px 15px rgba(184, 184, 230, 0.3),
      inset 0 1px 0 rgba(255, 248, 240, 0.5);
    z-index: -1;
    transition: all 0.3s ease;
  }
  
  .user-avator:hover::before {
    box-shadow: 
      0 0 0 3px rgba(184, 184, 230, 0.6),
      0 6px 20px rgba(184, 184, 230, 0.4),
      inset 0 1px 0 rgba(255, 248, 240, 0.6);
    transform: scale(1.05);
  }
  
  .user-avator img {
    display: block;
    width: 48px;
    height: 48px;
    border-radius: 50%;
    object-fit: cover;
    box-shadow: 
      0 4px 12px rgba(0, 0, 0, 0.15),
      inset 0 1px 0 rgba(255, 255, 255, 0.3);
    transition: all 0.3s ease;
    filter: brightness(1.05) contrast(0.95) saturate(0.9);
  }
  
  .user-avator:hover img {
    transform: scale(1.08);
    box-shadow: 
      0 6px 18px rgba(0, 0, 0, 0.2),
      inset 0 1px 0 rgba(255, 255, 255, 0.4);
    filter: brightness(1.1) contrast(0.98) saturate(0.95);
  }
  
  .el-dropdown-link {
    color: #1a2332;
    cursor: pointer;
    font-weight: 500;
    text-shadow: 
      0 2px 4px rgba(255, 255, 255, 0.9),
      0 0 8px rgba(255, 248, 240, 0.6),
      0 1px 2px rgba(255, 184, 140, 0.3);
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    gap: 5px;
    padding: 8px 15px;
    border-radius: 20px;
    background: rgba(255, 248, 240, 0.15);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 184, 140, 0.25);
  }
  
  .el-dropdown-link:hover {
    background: rgba(255, 248, 240, 0.25);
    border-color: rgba(255, 184, 140, 0.4);
    text-shadow: 
      0 2px 6px rgba(255, 255, 255, 1),
      0 0 12px rgba(255, 248, 240, 0.7),
      0 2px 3px rgba(255, 184, 140, 0.4);
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(255, 184, 140, 0.2);
  }
  
  .el-dropdown-link :deep(.el-icon) {
    transition: transform 0.3s ease;
  }
  
  .el-dropdown-link:hover :deep(.el-icon) {
    transform: translateY(2px);
  }
  
  /* 下拉菜单样式增强 */
  :deep(.el-dropdown-menu) {
    margin-top: 10px !important;
  }
  
  :deep(.el-dropdown-menu__item) {
    transition: all 0.3s ease !important;
  }
  
  :deep(.el-dropdown-menu__item:hover) {
    transform: translateX(5px) !important;
  }
</style>
