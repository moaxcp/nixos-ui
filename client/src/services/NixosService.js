import axios from 'axios'

export default {
    async getVersion() {
        let res = await axios.get('/nixos-version')
        return res.data;
    },
    async getOptions(attribute) {
        let res = await axios.get('/nixos-option/' + (attribute || ''))
        return res.data;
    }
}