const loadState = () => {
  try {
    const serializedState = localStorage.getItem("cc-state");
    const state = JSON.parse(serializedState) || {};
    // clear persisted error messages on load
    state.message = { message: "" };
    return state;
  } catch (err) {
    return {};
  }
};

const saveState = (state) => {
  try {
    const serializeState = JSON.stringify(state);
    localStorage.setItem("cc-state", serializeState);
  } catch (err) {
    console.log("Error saving state: ", err);
  }
};

module.exports = { loadState, saveState };
