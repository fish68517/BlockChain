const React = require("react");
const { useState, useEffect } = require("react");

function ValueEstimation({ listing, isEditable, saveEstimations }) {
  const [valueEstimation, setValueEstimation] = useState(
    listing?.valueEstimation || ""
  );
  const [repairCost, setRepairCost] = useState(
    listing?.repairCostEstimation || ""
  );
  const [message, setMessage] = useState("");
  const [error, setError] = useState(false);

  useEffect(() => {
    if (listing) {
      setValueEstimation(listing.valueEstimation || "");
      setRepairCost(listing.repairCostEstimation || "");
    }
  }, [listing]);

  useEffect(() => {
    let timeout;
    if (message !== "") {
      timeout = setTimeout(() => setMessage(""), 2000);
    }

    return clearTimeout(timeout);
  }, [message]);

  const onSubmitEstimations = () => {
    if (!valueEstimation || !repairCost) {
      return;
    }

    saveEstimations(valueEstimation, repairCost)
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
      <div className="form-group">
        <label htmlFor="car-value">Car value estimation</label>
        <input
          id="car-value"
          className="form-control"
          value={valueEstimation}
          readOnly={!isEditable}
          onChange={(e) => setValueEstimation(e.target.value)}
        />
      </div>
      <div className="form-group">
        <label htmlFor="repair-cost">Repair cost estimation</label>
        <input
          id="repair-cost"
          className="form-control"
          value={repairCost}
          readOnly={!isEditable}
          onChange={(e) => setRepairCost(e.target.value)}
        />
      </div>
      {isEditable && (
        <button
          className="btn btn-primary d-inline-block mt-1"
          onClick={onSubmitEstimations}
        >
          Save Estimations
        </button>
      )}
      {message && (
        <div
          className={`d-inline-block mt-2 p-2 alert alert-${
            error ? "error" : "success"
          }`}
        >
          {message}
        </div>
      )}
    </div>
  );
}

module.exports = ValueEstimation;
