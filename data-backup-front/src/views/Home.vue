<template>
  <div class="wrapper">
    <Top/>
    <div class="content-box">
      <!-- 背景柔光云层 -->
      <div class="bg-clouds">
        <div class="cloud cloud-1"></div>
        <div class="cloud cloud-2"></div>
        <div class="cloud cloud-3"></div>
      </div>
      <div class="content">
        <File/>
      </div>
    </div>
  </div>
</template>

<script setup name="Home">
  import Top from '@/components/Top.vue'
  import File from '@/components/File.vue'

</script>

<style scoped>
  .wrapper {
    width: 100%;
    height: 100%;
    position: relative;
    overflow: hidden;
  }
  
  .content-box {
    position: relative;
    overflow: hidden;
    padding-top: 10px;
  }
  
  /* 背景柔光云层 */
  .bg-clouds {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    pointer-events: none;
    z-index: 0;
    overflow: hidden;
  }
  
  .cloud {
    position: absolute;
    border-radius: 50%;
    filter: blur(60px);
    opacity: 0.4;
    animation: cloudDrift 30s ease-in-out infinite;
  }
  
  .cloud-1 {
    width: 400px;
    height: 300px;
    top: -50px;
    left: 10%;
    background: radial-gradient(ellipse, 
      rgba(255, 212, 196, 0.5) 0%, 
      rgba(255, 184, 140, 0.3) 40%, 
      transparent 70%);
    animation-duration: 35s;
    animation-delay: 0s;
  }
  
  .cloud-2 {
    width: 500px;
    height: 350px;
    top: 20%;
    right: 15%;
    background: radial-gradient(ellipse, 
      rgba(255, 248, 240, 0.4) 0%, 
      rgba(255, 212, 196, 0.3) 35%, 
      transparent 65%);
    animation-duration: 40s;
    animation-delay: -10s;
  }
  
  .cloud-3 {
    width: 350px;
    height: 280px;
    bottom: 10%;
    left: 20%;
    background: radial-gradient(ellipse, 
      rgba(184, 230, 184, 0.3) 0%, 
      rgba(230, 212, 184, 0.2) 40%, 
      transparent 70%);
    animation-duration: 45s;
    animation-delay: -20s;
  }
  
  @keyframes cloudDrift {
    0%, 100% {
      transform: translate(0, 0) scale(1);
      opacity: 0.4;
    }
    33% {
      transform: translate(50px, -30px) scale(1.1);
      opacity: 0.5;
    }
    66% {
      transform: translate(-30px, 20px) scale(0.95);
      opacity: 0.35;
    }
  }
  
  .content {
    position: relative;
    z-index: 1;
  }
</style>
