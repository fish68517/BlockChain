const React = require("react");
const { useState, useEffect } = require("react");

function Approve({ listing, isEditable, onSave, onReject }) {
  const [changeRequestMessage, setChangeRequestMessage] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState(false);

  useEffect(() => {
    let timeout;
    if (message !== "") {
      timeout = setTimeout(() => setMessage(""), 2000);
    }

    return () => clearTimeout(timeout);
  }, [message]);

  const onApprove = () => {
    onSave(true, true)
      .then(() => {
        setMessage("Saved!");
      })
      .catch(() => {
        setError(true);
        setMessage("Error saving value estimation.");
      });
  };

  const onRequestChanges = () => {
    onSave(false, true, changeRequestMessage)
      .then(() => {
        setMessage("Saved!");
      })
      .catch(() => {
        setError(true);
        setMessage("Error saving value estimation.");
      });
  };

  const onRejectListing = () => {
    onReject()
      .then(() => {
        setMessage("Saved!");
      })
      .catch(() => {
        setError(true);
        setMessage("Error saving value estimation.");
      });
  };

  return (
    <div>
      {isEditable && (
        <>
          <div className="form-group">
            <input
              className="form-control w-50 d-inline-block"
              value={changeRequestMessage}
              onChange={(e) => setChangeRequestMessage(e.target.value)}
            />
            <button className="btn btn-info ms-2" onClick={onRequestChanges}>
              Request Changes
            </button>
          </div>
          <button
            className="btn btn-primary d-inline-block mt-1"
            onClick={onApprove}
          >
            Approve
          </button>
          <button
            className="btn btn-danger d-inline-block mt-1 ms-1"
            onClick={onRejectListing}
          >
            Reject
          </button>
        </>
      )}
      {message && (
        <div
          className={`d-inline-block mt-2 ml-2 p-2 alert alert-${
            error ? "error" : "success"
          }`}
        >
          {message}
        </div>
      )}
    </div>
  );
}

module.exports = Approve;
