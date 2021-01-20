<template>
  <div id="version">
    <p>Version: {{ version.version }}</p>
  </div>
</template>

<script>
import NixosService from "@/services/NixosService.js"

export default {
  name: "Version",
  data() {
    return {
      version: { version: "unknown" }
    }
  },
  created() {
    this.updateVersion();
  },
  methods: {
    async updateVersion() {
      NixosService.getVersion().then(
        ((version) => {
          this.$set(this, "version", version)
        }).bind(this)
      );
    },
  },
};
</script>

<style>
</style>