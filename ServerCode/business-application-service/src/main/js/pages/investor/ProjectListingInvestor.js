const React = require("react");
const { Form } = require("react-bootstrap");
const { useState } = require("react");
const { connect } = require("react-redux");
const { investInCCProjectListing } = require("../../utils/web3Utils");
const MetaConnectModal = require("../../components/web3/MetaConnectModal");

const { createInvestmentsByUserID } = require("../../actions/Investments");
const { createInvestmentsByUserIDWithTransaction } = require("../../services/InvestmentsService");

const ProjectListingInvestor = ({ dispatch, projectListing, user }) => {
  const [showModal, setShowModal] = useState(false);

  const createInvestment = async (event) => {
    event.preventDefault();
    if (!amount) {
      setError("amount is required");
      return;
    }
    const investment = {
      amount: parseFloat(amount),
      listing: { id: projectListing.id },
    };

    const clearFields = () => {
      setAmount("");
    };

    let transactionHash = null;
    if (window.ethereum) {
      try {
        // Execute blockchain transaction and get transaction hash
        transactionHash = await investInCCProjectListing(projectListing.projectAddress, amount);
        
        // Use synchronized endpoint that verifies transaction and completes jBPM task
        const response = await createInvestmentsByUserIDWithTransaction(user.id, investment, transactionHash);
        
        clearFields();
        setError();
        setStatus(
          `Successfully created investment for listing ${projectListing.id}.`
        );
      } catch (e) {
        console.error("Investment error:", e);
        setError(e.response?.data || e.message || "Error creating investment. Transaction may have failed.");
      }
    } else {
      setShowModal(true);
      setError("Unable to make investment in Contract");
    }
  };

  const [amount, setAmount] = useState("");
  const [status, setStatus] = useState();
  const [error, setError] = useState();
  const { id, make, model, ccpg, fundingGoal, currFundingAmount, vin } =
    projectListing;

  return (
    <div className="card">
      <div className="card-header">
        <h5 className="mb-0">
          {make} {model}
        </h5>
      </div>
      <div className="card-body">
        <div className="text-small mb-3" style={{ color: '#718096' }}>VIN: {vin}</div>
        <div className="card-text mb-4">
          <div className="mb-2">
            <strong>CCPG: </strong>
            <span className="text-primary">{ccpg}</span>
          </div>
          <div className="mb-2">
            <strong>Initial Funding Goal: </strong>
            <span className="text-primary">{fundingGoal}</span>
          </div>
          <div className="mb-2">
            <strong>Current Funding Required: </strong>
            <span className="text-success fw-bold">{fundingGoal - currFundingAmount}</span>
          </div>
        </div>
        <div>
          <MetaConnectModal setShowModal={setShowModal} showModal={showModal} />
          <Form>
            <Form.Group className="mb-3">
              <Form.Label htmlFor="amount" className="fw-semibold">Investment Amount:</Form.Label>
              <Form.Control
                required
                id="amount-box"
                placeholder="Enter amount to invest"
                type="number"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                className="mb-3"
              />
            </Form.Group>
            <button className="btn btn-primary w-100" onClick={createInvestment}>
              Invest Now
            </button>
          </Form>
        </div>
        {status && (
          <div className="alert alert-success mt-3 mb-0">
            {status}
          </div>
        )}
        {error && (
          <div className="alert alert-danger mt-3 mb-0">
            {error}
          </div>
        )}
      </div>
    </div>
  );
};
const mapStateToProps = (state) => ({ user: state.auth.user });

module.exports = connect(mapStateToProps)(ProjectListingInvestor);
