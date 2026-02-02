const React = require('react');
const { useState } = require('react');

const AuctionBidInput = ({ id, onSubmitAuctionBid }) => {
  const [amount, setAmount] = useState(0);
  const inputStyle = {
    maxWidth: '300px',
  }

  return (
    <>
      <input
        className="form-control d-inline-block"
        style={inputStyle}
        value={amount}
        onChange={(e) => setAmount(e.target.value)}
      />
      <button className="btn btn-primary ms-1" onClick={() => onSubmitAuctionBid(id, amount)}>
        Bid
      </button>
    </>
  )
}

module.exports = AuctionBidInput;
