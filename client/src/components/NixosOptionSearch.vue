<template>
  <input
    type="text"
    id="searchTerm"
    v-on:keyup.enter="updateOptions()"
    v-model="searchTerm"
  />
</template>

<script>
  export default {
      props: ['searchTerm'],
      name: "NixosOptionSearch"
  }
</script>

<style></style>
