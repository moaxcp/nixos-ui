<template>
  <div id="nixos-option">
      <h1>Option</h1>
      <input 
        type="text" 
        id="searchTerm"
        v-on:keyup.enter="updateOptions()"
        v-model="searchTerm">
      <ul>
          <li v-for="option in options" v-bind:key="option">{{option}}</li>
      </ul>
  </div>
</template>

<script>
import NixosService from "@/services/NixosService.js"

export default {
    name: "NixosOption",
    data() {
        return {
            searchTerm: null,
            options: []
        }
    },
    created() {
        this.updateOptions();
    },
    methods: {
        async updateOptions() {
            NixosService.getOptions(this.searchTerm).then(
                ((options) => {
                    this.$set(this, "options", options)
                }).bind(this)
            )
        }
    }
}
</script>

<style>

</style>