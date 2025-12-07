const React = require("react");
const { Link } = require("react-router-dom");
const { connect } = require("react-redux");
const { Button, Card } = require("react-bootstrap");

const {
  getListingsPendingApproval,
  approveProject,
} = require("../../actions/ProjectListings");

const root = "/api";

class AdminPortal extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      attributes: [],
      page: 1,
      pageSize: 1,
      content: "ADMIN CONTENT",
      showApprovedProjects: false,
    };

    this.adminSock = React.createRef();

    this.toggleProjects = this.toggleProjects.bind(this);
    this.handleApproveBtn = this.handleApproveBtn.bind(this);
  }

  loadFromServer(pageSize) {
    this.props.dispatch(getListingsPendingApproval());
  }

  // show new projects or show approved projects.
  // This is used to handle the two buttons "New Projects" and "Approved Projects"
  toggleProjects(showApprovedProjects) {
    this.setState({ showApprovedProjects });
  }

  // change the status of project listing once it gets approved
  handleApproveBtn(id) {
    this.props.dispatch(approveProject(id));
  }

  componentDidMount() {
    this.loadFromServer(this.state.pageSize);
  }

  render() {
    const projectListingUI = this.state.showApprovedProjects ? (
      <ApprovedProjectListings projectListings={this.props.projectListings} />
    ) : (
      <ProjectListingList
        page={this.state.page}
        projectListings={this.props.projectListings}
        handleApproveBtn={this.handleApproveBtn}
      />
    );

    return (
      <div className="container">
        <header className="jumbotron">
          <h3>{this.state.content}</h3>
        </header>
        <div className="mb-2">
          <Button variant="primary" onClick={() => this.toggleProjects(false)}>
            All Projects
          </Button>{" "}
          <Button variant="success" onClick={() => this.toggleProjects(true)}>
            Approved Projects
          </Button>
        </div>
        {projectListingUI}
      </div>
    );
  }
}

class ProjectListingList extends React.Component {
  render() {
    const projectListings = this.props.projectListings.map((projectListing) => (
      <ProjectListing
        key={projectListing.id}
        projectListing={projectListing}
        handleApproveBtn={this.props.handleApproveBtn}
      />
    ));

    return (
      <div>
        <table>
          <tbody>
            <tr>
              <th>New Listing ID</th>
              {/* <th>Received Title?</th> */}
              <th>CCPG</th>
              <th>VIN Number</th>
              <th>Model</th>
              <th>Make</th>
              <th>Funding Goal</th>
              {/* <th>VIN Matched?</th> */}
              {/* <th>Listing Details Verified?</th> */}
              <th>User</th>
              <th>Pending tasks</th>
              {/* <th>User Role(s)</th> */}
            </tr>
            {projectListings}
          </tbody>
        </table>
      </div>
    );
  }
}

class ProjectListing extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      approved: false,
    };
  }

  showBooleanValue(boolean) {
    return boolean ? "Yes" : "No";
  }

  render() {
    const { projectListing } = this.props;

    return (
      <tr>
        <td>
          <Link to={`/listings/${projectListing.id}`}>{projectListing.id}</Link>
        </td>
        {/* <td>{this.showBooleanValue(projectListing.receiveTitle)}</td> */}
        <td>{projectListing.ccpg}</td>
        <td>{projectListing.vin}</td>
        <td>{projectListing.model}</td>
        <td>{projectListing.make}</td>
        <td>{projectListing.fundingGoal}</td>
        {/* <td>{this.showBooleanValue(projectListing.vinMatched)}</td> */}
        {/* <td>{this.showBooleanValue(projectListing.verifyDetails)}</td> */}
        <td>{projectListing.user.username}</td>
        {/* <td>{projectListing.user.roles[0].name}</td> <- TBD display all roles */}
        <td>
          {projectListing.pendingTasks.length == 0
            ? "Completed"
            : projectListing.pendingTasks.join(", ")}
        </td>
      </tr>
    );
  }
}

const ApprovedProjectListings = (props) => {
  const projectListings = props.projectListings.filter(
    (projectListing) =>
      projectListing["receiveTitle"] && projectListing["verifyDetails"]
  );

  return (
    <>
      {projectListings.map((projectListing) => (
        <ApprovedProjectListing
          key={projectListing.id}
          projectListing={projectListing}
        />
      ))}
    </>
  );
};

const ApprovedProjectListing = ({ projectListing }) => {
  return (
    <Card>
      <Card.Body>
        <Card.Title>
          <Link to={`/listings/${projectListing.id}`}>
            {projectListing.make} {projectListing.model}
          </Link>
        </Card.Title>
        <Card.Text>
          <small>
            <strong>CCPG:</strong> {projectListing.ccpg}
          </small>
          <br />
          <small>
            <strong>Funding Goal:</strong> {projectListing.fundingGoal}
          </small>
          <br />
          <small>
            <strong>Funding Progress:</strong>{" "}
            {projectListing.currFundingAmount}
          </small>
          <br />
        </Card.Text>
      </Card.Body>
    </Card>
  );
};

const mapStateToProps = (state) => ({
  user: state.auth.user,
  projectListings: state.projectListings,
});

connect()(ProjectListing);
module.exports = connect(mapStateToProps)(AdminPortal);
