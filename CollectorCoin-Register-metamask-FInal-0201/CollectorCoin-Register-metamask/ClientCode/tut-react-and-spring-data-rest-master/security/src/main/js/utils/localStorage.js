const loadState = () => {
  try {
    const serializedState = localStorage.getItem('cc-state')
    return JSON.parse(serializedState) || {};
  }
  catch (err) {
    return {};
  }
}

const saveState = (state) => {
  try {
    const serializeState = JSON.stringify(state);
    localStorage.setItem('cc-state', serializeState);
  }
  catch (err) {
    console.log('Error saving state: ', err);
  }
}

module.exports = { loadState, saveState };
