const React = require('react');
const { Form } = require('react-bootstrap');
const { useState } = require('react');
const { connect } = require('react-redux');
const { investInCCProjectListing } = require('../../utils/web3Utils');
const MetaConnectModal = require('../../components/web3/MetaConnectModal');

const { createInvestmentsByUserID } = require('../../actions/Investments');

const ProjectListingInvestor = ({ dispatch, projectListing, user }) => { 
  const [showModal, setShowModal] = useState(false);

  const createInvestment = async (event) => {
    event.preventDefault();
    if (!amount) {
      setError('amount is required')
      return;
    }
    const investment = {
      amount: parseFloat(amount),
      listing: { id: projectListing.id },
    }

    const clearFields = () => {
      setAmount('');
    }

    if(window.ethereum) {
      await investInCCProjectListing(projectListing.projectAddress, amount);
    } else {
      setShowModal(true);
      setError("Unable to make investment in Contract");
      throw "Unable to make investment in Contract";
    }

    dispatch(createInvestmentsByUserID(user.id, investment))
    .then(() => {
      clearFields();
      setError();
      setStatus(`Successfully created investment for listing ${projectListing.id}.`)
    }).catch(e => {
      console.log(e);
      setError("Error creating investment.");
    });
  };

  const [amount, setAmount] = useState('');
  const [status, setStatus] = useState();
  const [error, setError] = useState();
  const { id, make, model, ccpg, fundingGoal, currFundingAmount, vin } = projectListing;

  return (
    <div className="card mt-2">
      <div className="card-body">
        <div className="card-title">
            <h5>{make}{' '}{model}</h5>
          <div className="text-small">VIN: {vin}</div>
        </div>
        <div className="card-text">
          <div>
            <strong>CCPG: </strong>
            {ccpg}
          </div>
          <div>
            <strong>Initial Funding Goal: </strong>
            {fundingGoal}
          </div>
          <div>
            <strong>Current Funding Required: </strong>
            {fundingGoal - currFundingAmount}
          </div>
        </div>
        <div>
          <MetaConnectModal setShowModal={setShowModal} showModal={showModal} />
          <Form>
              <Form.Group>
                <Form.Label htmlFor="amount">Amount:</Form.Label>
                <Form.Control
                  required
                  id="amount-box"
                  placeholder='Investment amount'
                  type="input"
                  value={amount}
                  onChange={e => setAmount(e.target.value)}
                />
              </Form.Group>
            <button className="btn btn-link p-0" onClick={createInvestment}>Invest</button>
          </Form>
        </div>
      </div>
      {status && <div className="d-inline-block mt-2 p-2 alert alert-success">{status}</div>}
      {error && <div className="d-inline-block mt-2 p-2 alert alert-danger">{error}</div>}
    </div>
  )
}
const mapStateToProps = (state) => ({ user: state.auth.user });

module.exports = connect(mapStateToProps)(ProjectListingInvestor);
