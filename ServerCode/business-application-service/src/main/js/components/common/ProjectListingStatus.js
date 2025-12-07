const React = require("react");

function ProjectStatusBadge({ listing }) {
  const isEditable = listing.pendingTasks.includes("Edit Details");
  const isRejected = listing.pendingTasks.includes("Rejected");

  if (isRejected) {
    return <span className="badge bg-danger">Rejected</span>;
  }

  if (isEditable) {
    return <span className="badge bg-warning">Pending owner edits</span>;
  }

  if (listing.valueEstimation) {
    return <span className="badge bg-success">Passed value estimation</span>;
  }

  if (listing.receiveTitle && listing.verifyDetails) {
    return <span className="badge bg-success">Approved</span>;
  }

  if (!listing.receiveTitle && !listing.verifyDetails) {
    return <span className="badge bg-info">Pending Approval</span>;
  }

  return null;
}

module.exports = ProjectStatusBadge;
