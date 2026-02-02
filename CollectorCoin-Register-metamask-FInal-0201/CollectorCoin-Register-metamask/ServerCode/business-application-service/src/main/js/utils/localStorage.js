const loadState = () => {
  try {
    const serializedState = localStorage.getItem("cc-state");
    const state = JSON.parse(serializedState) || {};
    // clear persisted error messages on load
    state.message = { message: "" };
    // Reset any loading states that might have been persisted
    if (state.auth) {
      state.auth.loading = false;
    }
    return state;
  } catch (err) {
    return {};
  }
};

const saveState = (state) => {
  try {
    // Create a copy to avoid mutating the original state
    const stateToPersist = JSON.parse(JSON.stringify(state));
    // Don't persist loading states
    if (stateToPersist.auth) {
      delete stateToPersist.auth.loading;
    }
    const serializeState = JSON.stringify(stateToPersist);
    localStorage.setItem("cc-state", serializeState);
  } catch (err) {
    console.log("Error saving state: ", err);
  }
};

module.exports = { loadState, saveState };