import axios from 'axios'

export default {
    async getVersion() {
        let res = await axios.get('http://localhost:8080/nixos-version')
        return res.data;
    },
    async getOptions(attribute) {
        let res = await axios.get('http://localhost:8080/nixos-option/' + (attribute || ''))
        return res.data;
    }
}