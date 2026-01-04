<template>
  <div class="sidebar">
    <el-menu 
    default-active="Upload" 
    class="sidebar-el-menu"
    background-color="#EBEDF0"
    
    active-text-color="#20a0ff"
    unique-opened 
    router
    >
      <el-menu-item index="/Home/Upload">
        <el-icon><Files/></el-icon>
        <span>备份还原</span>
      </el-menu-item>

      <el-menu-item index="/Home/Zip">
        <el-icon><SwitchFilled/></el-icon>
        <span>压缩解压</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup name="Sidebar">
  import { Files, SwitchFilled  } from '@element-plus/icons-vue'

</script>

<style scoped>
  .sidebar {
    display: block;
    position: absolute;
    left: 0px;
    top: 80px;
    bottom: 0;
    overflow-y: scroll;
    background: rgba(255, 248, 240, 0.6);
    backdrop-filter: blur(15px);
    border-right: 1px solid rgba(184, 184, 230, 0.3);
    box-shadow: 2px 0 15px rgba(184, 184, 230, 0.15);
  }
  
  .sidebar::-webkit-scrollbar {
    width: 6px;
  }
  
  .sidebar::-webkit-scrollbar-track {
    background: rgba(255, 248, 240, 0.1);
    border-radius: 10px;
  }
  
  .sidebar::-webkit-scrollbar-thumb {
    background: rgba(184, 184, 230, 0.4);
    border-radius: 10px;
  }
  
  .sidebar::-webkit-scrollbar-thumb:hover {
    background: rgba(184, 184, 230, 0.6);
  }
  
  .sidebar-el-menu:not(.el-menu--collapse) {
    width: 220px;
    background: transparent !important;
    border-right: none !important;
  }
  
  .sidebar > ul {
    height: 100%;
  }
  
  /* 菜单项样式 */
  :deep(.el-menu-item) {
    color: #1a2332 !important;
    border-radius: 12px !important;
    margin: 5px 10px !important;
    transition: all 0.3s ease !important;
    background: rgba(255, 248, 240, 0.5) !important;
    backdrop-filter: blur(10px) !important;
  }
  
  :deep(.el-menu-item:hover) {
    background: rgba(184, 184, 230, 0.3) !important;
    color: #1a2332 !important;
    transform: translateX(5px);
    box-shadow: 0 4px 12px rgba(184, 184, 230, 0.2) !important;
  }
  
  :deep(.el-menu-item.is-active) {
    background: linear-gradient(135deg, rgba(184, 184, 230, 0.4), rgba(168, 197, 232, 0.3)) !important;
    color: #1a2332 !important;
    font-weight: 600 !important;
    box-shadow: 0 4px 15px rgba(184, 184, 230, 0.25) !important;
  }
  
  :deep(.el-menu-item .el-icon) {
    color: inherit !important;
    margin-right: 8px;
  }
</style>