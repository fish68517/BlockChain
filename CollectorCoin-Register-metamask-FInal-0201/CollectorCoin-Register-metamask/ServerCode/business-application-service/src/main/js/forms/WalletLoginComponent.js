const React = require("react");
const { useState, useEffect } = require("react");
const { useNavigate } = require("react-router-dom");
const { connect } = require("react-redux");

const {
  connectWallet,
  walletLogin,
  walletRegister,
} = require("../actions/WalletAuth");
const WalletAuthService = require("../services/WalletAuthService");

const availableRoles = ["owner", "investor", "restorer", "buyer"];

function WalletLogin(props) {
  const [step, setStep] = useState("loading");
  const [walletAddress, setWalletAddress] = useState("");
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [role, setRole] = useState("investor");

  const navigate = useNavigate();

  const checkMetaMask = () => {
    return (
      typeof window !== "undefined" &&
      typeof window.ethereum !== "undefined" &&
      window.ethereum.isMetaMask
    );
  };

  useEffect(() => {
    const initializeWalletState = async () => {
      if (!checkMetaMask()) {
        setStep("no-metamask");
        return;
      }

      try {
        const accounts = await window.ethereum.request({
          method: "eth_accounts",
        });

        if (accounts && accounts.length > 0) {
          const address = accounts[0];
          setWalletAddress(address);

          const response = await WalletAuthService.checkWalletExists(address);

          if (response.data.exists) {
            setStep("existing-user");
          } else {
            setStep("register");
          }
        } else {
          setStep("initial");
        }
      } catch (error) {
        console.error("Error checking wallet state:", error);
        setStep("initial");
      }
    };

    initializeWalletState();
  }, []);

  const handleConnectWallet = async () => {
    setLoading(true);
    setErrorMessage("");

    try {
      const { dispatch } = props;
      const result = await dispatch(connectWallet());

      setWalletAddress(result.walletAddress);

      if (result.isRegistered) {
        await handleWalletLogin(result.walletAddress);
      } else {
        setStep("register");
        setLoading(false);
      }
    } catch (error) {
      setErrorMessage(error.message || "Failed to connect wallet");
      setLoading(false);
    }
  };

  const handleWalletLogin = async (address) => {
    // 打印
    console.log("handleWalletLogin：", address);
    setLoading(true);
    setErrorMessage("");

    try {
      const { dispatch } = props;
      await dispatch(walletLogin(address || walletAddress));
      navigate("/");
    } catch (error) {
      setErrorMessage(error.message || "Login failed");
      setLoading(false);
    }
  };

  const handleWalletRegister = async (e) => {
    e.preventDefault();
    setLoading(true);
    setErrorMessage("");

    try {
      const { dispatch } = props;
      await dispatch(walletRegister(walletAddress, username, email, role));
      navigate("/");
    } catch (error) {
      setErrorMessage(
        typeof error === "string"
          ? error
          : error.message || "Registration failed"
      );
      setLoading(false);
    }
  };

  const handleInstallMetaMask = () => {
    window.open("https://metamask.io/download/", "_blank");
  };

  const handleDisconnect = async () => {
    try {
      await window.ethereum.request({
        method: "wallet_revokePermissions",
        params: [{ eth_accounts: {} }],
      });
      setStep("initial");
      setWalletAddress("");
    } catch (error) {
      setStep("initial");
      setWalletAddress("");
    }
  };

  // Loading state
  if (step === "loading") {
    return (
      <div className="col-md-12">
        <div className="card card-container">
          <div className="text-center">
            <span className="spinner-border"></span>
            <p>Checking wallet status...</p>
          </div>
        </div>
      </div>
    );
  }

  // No MetaMask installed
  if (step === "no-metamask") {
    return (
      <div className="col-md-12">
        <div className="card card-container">
          <div className="text-center">
            <img
              src="https://upload.wikimedia.org/wikipedia/commons/3/36/MetaMask_Fox.svg"
              alt="MetaMask"
              style={{ width: "80px", marginBottom: "20px" }}
            />
            <h4>MetaMask Required</h4>
            <p>
              To use this application, please install the MetaMask browser
              extension.
            </p>

            <button
              className="btn btn-warning btn-block"
              onClick={handleInstallMetaMask}
            >
              Install MetaMask
            </button>
          </div>
        </div>
      </div>
    );
  }

  // Existing user
  if (step === "existing-user") {
    return (
      <div className="col-md-12">
        <div className="card card-container">
          <div className="text-center">
            <img
              src="https://upload.wikimedia.org/wikipedia/commons/3/36/MetaMask_Fox.svg"
              alt="MetaMask"
              style={{ width: "80px", marginBottom: "20px" }}
            />
            <h4>Welcome Back!</h4>
            <p className="text-muted" style={{ fontSize: "12px" }}>
              Wallet: {walletAddress.slice(0, 6)}...{walletAddress.slice(-4)}
            </p>

            <button
              className="btn btn-warning btn-block"
              onClick={() => handleWalletLogin(walletAddress)}
              disabled={loading}
              style={{ marginBottom: "10px" }}
            >
              {loading ? (
                <>
                  <span className="spinner-border spinner-border-sm"></span>
                  {" Signing in..."}
                </>
              ) : (
                "Sign In with Wallet"
              )}
            </button>

            <button
              className="btn btn-outline-secondary btn-block"
              onClick={handleDisconnect}
            >
              Use Different Wallet
            </button>

            {errorMessage && (
              <div className="alert alert-danger mt-3" role="alert">
                {errorMessage}
              </div>
            )}
          </div>
        </div>
      </div>
    );
  }

  // Registration form
  if (step === "register") {
    return (
      <div className="col-md-12">
        <div className="card card-container">
          <h4 className="text-center">Complete Registration</h4>
          <p className="text-center text-muted" style={{ fontSize: "12px" }}>
            Wallet: {walletAddress.slice(0, 6)}...{walletAddress.slice(-4)}
          </p>

          <form onSubmit={handleWalletRegister}>
            <div className="form-group">
              <label htmlFor="username">Username</label>
              <input
                type="text"
                className="form-control"
                name="username"
                required
                minLength={3}
                maxLength={20}
                value={username}
                onChange={(e) => setUsername(e.target.value)}
              />
            </div>

            <div className="form-group">
              <label htmlFor="email">Email</label>
              <input
                type="email"
                className="form-control"
                name="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>

            <div className="form-group">
              <label htmlFor="role">Role</label>
              <select
                className="form-select"
                name="role"
                required
                value={role}
                onChange={(e) => setRole(e.target.value)}
              >
                {availableRoles.map((r) => (
                  <option key={r} value={r}>
                    {r.charAt(0).toUpperCase() + r.slice(1)}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <button
                className="btn btn-primary btn-block"
                type="submit"
                disabled={loading || !username || !email}
              >
                {loading ? (
                  <>
                    <span className="spinner-border spinner-border-sm"></span>
                    {" Registering..."}
                  </>
                ) : (
                  "Sign Message & Register"
                )}
              </button>
            </div>

            <button
              type="button"
              className="btn btn-outline-secondary btn-block mt-2"
              onClick={handleDisconnect}
            >
              Use Different Wallet
            </button>

            {errorMessage && (
              <div className="alert alert-danger mt-3" role="alert">
                {errorMessage}
              </div>
            )}
          </form>
        </div>
      </div>
    );
  }

  // Connect wallet button (initial state)
  return (
    <div className="col-md-12">
      <div className="card card-container">
        <div className="text-center">
          <img
            src="https://upload.wikimedia.org/wikipedia/commons/3/36/MetaMask_Fox.svg"
            alt="MetaMask"
            style={{ width: "80px", marginBottom: "20px" }}
          />
          <h4>Connect with MetaMask</h4>
          <p>Sign in securely using your wallet</p>

          <button
            className="btn btn-warning btn-block"
            onClick={handleConnectWallet}
            disabled={loading}
          >
            {loading ? (
              <>
                <span className="spinner-border spinner-border-sm"></span>
                {" Connecting..."}
              </>
            ) : (
              "Connect Wallet"
            )}
          </button>

          {errorMessage && (
            <div className="alert alert-danger mt-3" role="alert">
              {errorMessage}
            </div>
          )}

          <hr />

          <button
            className="btn btn-outline-secondary btn-block"
            onClick={() => navigate("/admin-login")}
          >
            🔐 Login as Admin
          </button>
        </div>
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

module.exports = connect(mapStateToProps)(WalletLogin);
