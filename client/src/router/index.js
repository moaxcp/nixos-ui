import Vue from 'vue'
import VueRouter from 'vue-router'
import Version from '../views/Version.vue'
import NixosOption from '../views/NixosOption.vue'
import NixosTerminal from '../views/NixosTerminal.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'version',
    component: Version
  },
  {
    path: '/options/:option?',
    name: 'options',
    component: NixosOption,
    props: true
  },
  {
    path: '/terminal',
    name: 'terminal',
    component: NixosTerminal,
    props: true
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
