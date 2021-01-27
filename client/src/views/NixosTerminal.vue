<template>
  <div id="terminal">
  </div>
</template>

<script>
import {Terminal} from 'xterm'
import { WebLinksAddon } from 'xterm-addon-web-links'
import { FitAddon } from 'xterm-addon-fit'
import { AttachAddon } from 'xterm-addon-attach'
export default {
  name: 'NixosTerminal',
  data() {
    return {
      terminal: new Terminal({cursorBlink: 'block'}),
      webSocket: new WebSocket('ws://localhost:8080/terminal')
    }
  },
  mounted() {
    this.terminal.loadAddon(new WebLinksAddon())
    const fitAddon = new FitAddon()
    const attachAddon = new AttachAddon(this.webSocket)
    this.terminal.loadAddon(attachAddon)
    this.terminal.loadAddon(fitAddon)
    
    this.terminal.on('key', (key, ev) => {
        console.log(key.charCodeAt(0) + ' ' + ev);
        if (key.charCodeAt(0) == 13)
            this.terminal.write('\n');
        this.terminal.write(key);
    })
    this.terminal.open(this.$el, true)
    fitAddon.fit()
    this.terminal.write('Hello from \x1B[1;3;31mxterm.js\x1B[0m $ ')
    this.terminal.write('http://github.com/')
  },
  unmounted() {
    this.websocket.destroy()
    this.terminal.destroy()
  }
}
</script>

<style src="xterm/css/xterm.css">
<style>

</style>