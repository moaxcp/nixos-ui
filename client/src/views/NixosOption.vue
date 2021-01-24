<template>
  <div id="nixos-option">
    <h1>Option</h1>
    <div v-if="option">
      <a href="javascript:void(0);" class="option-path" @click="clearSearchTerm()">options</a> ->
      <template v-for="(record, index) in searchPaths">
          <a href="javascript:void(0);" :class="index == searchPaths.length - 1 ? 'active-option-path' : 'option-path'" @click="updateSearchTerm(record)" v-bind:key="index">{{ record.option }}</a>
          {{ index == searchPaths.length - 1 ? '' : '->' }}
      </template>
    </div>
    <input
      type="text"
      id="searchTerm"
      v-on:keyup.enter="updateOptions()"
      v-model="searchTerm"
    />
    <button @click="updateOptions()">?</button>
    <ul>
      <li v-for="option in options" v-bind:key="option">{{ option }}</li>
    </ul>
  </div>
</template>

<script>
  import NixosService from '@/services/NixosService.js'
  import * as optionPath from '@/model/option-path.js'

  export default {
    props: ['option'],
    name: 'NixosOption',
    data() {
      return {
        searchTerm: this.option,
        options: []
      }
    },
    computed: {
        searchPaths() {
            if(this.option) {
              return optionPath.getPaths(this.option)
            }
            return []
        }
    },
    created() {
      this.updateOptions()
    },
    methods: {
      clearSearchTerm() {
        this.searchTerm = ''
        this.updateOptions()
      },
      updateSearchTerm(record) {
        this.searchTerm = record.path
        this.updateOptions()
      },
      async updateOptions() {
        this.updateRoute()
        NixosService.getOptions(this.searchTerm).then(
          ((options) => {
            this.$set(this, 'options', options)
          }).bind(this)
        )
      },
      updateRoute() {
        let path
        if (this.searchTerm == '') {
          path = { name: 'options'}
        } else {
          path = { name: 'options', params: { option: this.searchTerm } }
        }
        if (this.$route.path !== path) {
          this.$router.push(path).catch((err) => {
            if (err.name !== 'NavigationDuplicated') {
              throw err
            }
          })
        }
      }
    }
  }
</script>

<style scoped>

.option-path {
  font-weight: bold;
  color: #415e9a;
}

.active-option-path {
  color: #699ad7;
}

</style>
