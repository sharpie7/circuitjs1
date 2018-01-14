  // To deal with this bug: https://github.com/electron/electron/issues/11053
  Object.defineProperty(navigator, 'languages', {
    value: ["en-US"],
    configurable: true
});