const React = require("react");
const { connect } = require("react-redux");
const { CLEAR_MESSAGE } = require("../actions/Types");

function ErrorComponent({ message, clearMessage }) {
  if (!message) return null;

  return (
    <div
      className="alert alert-danger alert-dismissible fade show"
      role="alert"
    >
      {message}
      <button
        type="button"
        className="btn-close"
        aria-label="Close"
        onClick={clearMessage}
      ></button>
    </div>
  );
}

function mapStateToProps(state) {
  return { message: state.message.message };
}

function mapDispatchToProps(dispatch) {
  return {
    clearMessage: () => dispatch({ type: CLEAR_MESSAGE }),
  };
}

module.exports = connect(mapStateToProps, mapDispatchToProps)(ErrorComponent);
