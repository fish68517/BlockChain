const React = require("react");
const { useState, useEffect } = require("react");
const { useNavigate } = require("react-router-dom");
const { connect } = require("react-redux");

const { login } = require("../actions/Auth");

const required = (value) => {
  if (!value) {
    return (
      <div className="alert alert-danger" role="alert">
        This field is required!
      </div>
    );
  }
};

function Login(props) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const navigate = useNavigate();

  const onChangeUsername = (e) => {
    setUsername(e.target.value);
  };

  const onChangePassword = (e) => {
    setPassword(e.target.value);
  };

  const onRegisterClicked = () => {
    navigate("/register");
  };

  const handleLogin = (e) => {
    e.preventDefault();
    const { dispatch } = props;
    setLoading(true);
    setErrorMessage("");
    dispatch(login(username, password))
      .then(() => navigate("/"))
      .catch(() => {
        setLoading(false);
        console.log("handleLogin", props);
        console.log(e);
        setErrorMessage("Bad credentials");
      });
  };

  return (
    <div className="col-md-12">
      <div className="card card-container">
        <form onSubmit={handleLogin}>
          <div className="form-group">
            <label htmlFor="username">Username</label>
            <input
              type="text"
              className="form-control"
              name="username"
              value={username}
              onChange={onChangeUsername}
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              type="password"
              className="form-control"
              name="password"
              value={password}
              onChange={onChangePassword}
            />
          </div>

          <div className="form-group">
            <button
              className="btn btn-primary btn-block"
              disabled={loading || !username || !password}
              type="submit"
            >
              {loading && (
                <span className="spinner-border spinner-border-sm"></span>
              )}
              <span>Login</span>
            </button>

            <button
              className="btn btn-primary btn-block"
              onClick={onRegisterClicked}
            >
              <span>Register</span>
            </button>
          </div>

          {errorMessage && (
            <div className="form-group">
              <div className="alert alert-danger" role="alert">
                {errorMessage}
              </div>
            </div>
          )}
        </form>
      </div>
    </div>
  );
}

function mapStateToProps(state) {
  const { isLoggedIn } = state.auth;
  const { message } = state.message;
  return {
    isLoggedIn,
    message,
  };
}

module.exports = connect(mapStateToProps)(Login);
