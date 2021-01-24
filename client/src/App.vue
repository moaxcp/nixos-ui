<template>
  <div id="app">
    <img id="logo" alt="NixOS" src="./assets/nixos.svg" />
    <div id="nav">
      <router-link :to="{ name: 'version' }">Version</router-link> |
      <router-link :to="{ name: 'options' }">Options</router-link> |
      <router-link :to="{ name: 'terminal' }">Terminal</router-link>
    </div>
    <router-view/>
  </div>
</template>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

#logo {
  width: 100%;
}

#nav {
  padding: 30px;
}

#nav a {
  font-weight: bold;
  color: #415e9a;
}

#nav a.router-link-exact-active {
  color: #699ad7;
}
</style>
