import * as optionPath from '../../src/model/option-path.js'

test('a has paths [a]', () => {
  expect(optionPath.getPaths('a')).toEqual([{option: 'a', path: 'a'}])
})

test('a.b has paths [a, a.b]', () => {
  expect(optionPath.getPaths('a.b')).toEqual([{option: 'a', path: 'a'}, {option: 'b', path: 'a.b'}])
})

test('a.b.c has paths [a, a.b, a.b.c]', () => {
  expect(optionPath.getPaths('a.b.c')).toEqual([{option: 'a', path: 'a'}, {option: 'b', path: 'a.b'}, {option: 'c', path: 'a.b.c'}])
})

test('system.boot.launcher has paths [system, system.boot, system.boot.launcher]', () => {
  expect(optionPath.getPaths('system.boot.launcher')).toEqual([{option: 'system', path: 'system'}, {option: 'boot', path: 'system.boot'}, {option: 'launcher', path: 'system.boot.launcher'}])
})