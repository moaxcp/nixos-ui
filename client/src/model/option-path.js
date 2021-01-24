export function getPaths(path) {
    if(!path.indexOf('.') == -1) {
        return [path]
    }
    const parts = path.split('.')
    return parts.map(p => getPathFor(parts, p))
}

function getPathFor(parts, last) {
    let result = ''
    for(let part in parts) {
        result += parts[part]
        if(parts[part] == last) {
            break
        }
        result += '.'
    }
    return { option: last, path: result }
}