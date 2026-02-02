const React = require('react');
const { useState, useEffect } = require('react');

function Approve({ listing, isEditable, onSave, onReject }) {
  // const [areDetailsVerified, setAreDetailsVerified] = useState(listing?.verifyDetails || false);
  // const [isTitleReceived, setIsTitleReceived] = useState(listing?.receiveTitle || false);
  const [changeRequestMessage, setChangeRequestMessage] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState(false);

  // useEffect(() => {
  //   if (listing) {
  //     setAreDetailsVerified(listing.verifyDetails);
  //     setIsTitleReceived(listing.receiveTitle);
  //   }
  // }, [listing]);

  useEffect(() => {
    let timeout;
    if (message !== '') {
      timeout = setTimeout(() => setMessage(''), 2000);
    }

    return () => clearTimeout(timeout);
  }, [message]);

  const onApprove = () => {
    onSave(true, true)
    .then(() => {
      setMessage('Saved!');
    })
    .catch(() => {
      setError(true);
      setMessage('Error saving value estimation.');
    });
  }

  const onRequestChanges = () => {
    onSave(false, true, changeRequestMessage)
    .then(() => {
      setMessage('Saved!');
    })
    .catch(() => {
      setError(true);
      setMessage('Error saving value estimation.');
    });
  }

  const onRejectListing = () => {
    onReject()
    .then(() => {
      setMessage('Saved!');
    })
    .catch(() => {
      setError(true);
      setMessage('Error saving value estimation.');
    });
  }

  return (
    <div>
      {/* <div className="form-check">
        <input
          className="form-check-input"
          type="checkbox"
          checked={areDetailsVerified}
          value={areDetailsVerified}
          onChange={() => isEditable && setAreDetailsVerified(!areDetailsVerified)}
          id="detailsCheck"
        />
        <label className="form-check-label" htmlFor="detailsCheck">
          Details Verified
        </label>
      </div>
      <div className="form-check">
        <input
          className="form-check-input"
          type="checkbox"
          checked={isTitleReceived}
          value={isTitleReceived}
          onChange={() => isEditable && setIsTitleReceived(!isTitleReceived)}
          id="titleCheck"
        />
        <label className="form-check-label" htmlFor="titleCheck">
          Title Received
        </label>
      </div> */}
      {isEditable &&
        <>
          <div className="form-group">
            <input
              className="form-control w-50 d-inline-block"
              value={changeRequestMessage}
              onChange={e => setChangeRequestMessage(e.target.value)}
            />
            <button className="btn btn-info ms-2" onClick={onRequestChanges}>Request Changes</button>
          </div>
          <button className="btn btn-primary d-inline-block mt-1" onClick={onApprove}>Approve</button>
          <button className="btn btn-danger d-inline-block mt-1 ms-1" onClick={onRejectListing}>Reject</button>
        </>
      }
      {message &&
        <div className={`d-inline-block mt-2 ml-2 p-2 alert alert-${error ? 'error' : 'success'}`}>{message}</div>
      }
    </div>
  )
}

module.exports = Approve;
