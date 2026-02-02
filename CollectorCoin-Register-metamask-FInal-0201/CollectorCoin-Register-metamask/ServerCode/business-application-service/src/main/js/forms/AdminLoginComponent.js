const React = require("react");
const { useState } = require("react");
const { useNavigate } = require("react-router-dom");
const { connect } = require("react-redux");

const { login } = require("../actions/Auth");

function AdminLogin(props) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const navigate = useNavigate();

  const handleLogin = (e) => {
    e.preventDefault();
    const { dispatch } = props;
    setLoading(true);
    setErrorMessage("");

    dispatch(login(username, password))
      .then(() => {
        // Store user in localStorage for persistence
        const user = props.user;
        if (user) {
          localStorage.setItem("user", JSON.stringify(user));
        }
        navigate("/");
      })
      .catch((error) => {
        setLoading(false);
        setErrorMessage("Invalid admin credentials");
      });
  };

  const handleBackToWalletLogin = () => {
    navigate("/wallet-login");
  };

  return (
    <div className="col-md-12">
      <div className="card card-container">
        <div className="text-center" style={{ marginBottom: "20px" }}>
          <h4>🔐 Admin Login</h4>
          <p className="text-muted">Login with your admin credentials</p>
        </div>

        <form onSubmit={handleLogin}>
          <div className="form-group">
            <label htmlFor="username">Username</label>
            <input
              type="text"
              className="form-control"
              name="username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              type="password"
              className="form-control"
              name="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <button
              className="btn btn-primary btn-block"
              disabled={loading || !username || !password}
              type="submit"
            >
              {loading ? (
                <>
                  <span className="spinner-border spinner-border-sm"></span>
                  {" Logging in..."}
                </>
              ) : (
                "Login as Admin"
              )}
            </button>
          </div>

          {errorMessage && (
            <div className="alert alert-danger" role="alert">
              {errorMessage}
            </div>
          )}

          <hr />

          <button
            type="button"
            className="btn btn-outline-secondary btn-block"
            onClick={handleBackToWalletLogin}
          >
            ← Back to Wallet Login
          </button>
        </form>
      </div>
    </div>
  );
}

function mapStateToProps(state) {
  const { isLoggedIn, user } = state.auth;
  const { message } = state.message;
  return {
    isLoggedIn,
    user,
    message,
  };
}

module.exports = connect(mapStateToProps)(AdminLogin);