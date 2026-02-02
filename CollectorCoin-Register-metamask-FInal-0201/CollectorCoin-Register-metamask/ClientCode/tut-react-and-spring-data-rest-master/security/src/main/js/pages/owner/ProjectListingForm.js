const React = require('react');
const { useState, useRef, useEffect } = require('react');

const {
  Row,
  Col,
  Button,
  Form
} = require('react-bootstrap');

// is listing is defined - form is in editing an existing listing
const ProjectListingForm = ({ listing, onSubmit, onError, onFileClick }) => {
  const [make, setMake] = useState(listing ? listing.make : '');
  const [model, setModel] = useState(listing ? listing.model : '');
  const [vin, setVin] = useState(listing ? listing.vin : '');
  const [ccpg, setCCPG] = useState(listing ? listing.ccpg : '');
  const [fundingGoal, setFundingGoal] = useState(listing ? listing.fundingGoal : '');
  const [description, setDescription] = useState(listing ? listing.description : '');
  const [consent, setConsent] = useState(true);
  const [file, setFile] = useState(null);
  const fileInputRef = useRef();

  useEffect(() => {
    if (listing) {
      setMake(listing.make);
      setModel(listing.model);
      setVin(listing.vin);
      setFundingGoal(listing.fundingGoal);
      setCCPG(listing.ccpg);
      setDescription(listing.description);
    }
  }, [listing]);

  const clearFields = () => {
    setMake('');
    setModel('');
    setVin('');
    setFundingGoal('');
    setCCPG('');
    setDescription('');
    setFile(null);
    setConsent(false);

    if (fileInputRef.current) {
      fileInputRef.current.value = null;
    }
  }

  const isFormValid = () => {
    if (
      !make ||
      !model ||
      !vin ||
      !ccpg ||
      !fundingGoal
    ) {
      onError('Please fill out required fields.')
      return false;
    }

    if (!file && !listing) {
      onError('Please upload proof of ownership.')
      return false;
    }

    if (!consent) {
      onError('Please check the consent box.');
      return false;
    }

    return true;
  }

  const createProjectListing = (event) => {
    event.preventDefault();
    if (!isFormValid()) {
      return;
    }
    const listing = {
      vin,
      make,
      model,
      ccpg,
      fundingGoal,
      vinMatched: true,
      description,
    }

    const formData = new FormData();
    formData.append('titleFile', file);
    formData.append('listing', JSON.stringify(listing));

    onSubmit(formData);
    clearFields();
  };

  return (
    <Form>
      <Form.Group>
        <Row className="justify-content-md-center">
          <Col xs={6}>
            <Form.Label htmlFor="car-make">Car Make</Form.Label>
            <Form.Control
              required
              id="car-make"
              placeholder="Car Make"
              name="make"
              value={make}
              onChange={e => setMake(e.target.value)}
              type="input"
            />
          </Col>
          <Col xs={6}>
            <Form.Label htmlFor="car-model">Car Model</Form.Label>
            <Form.Control
              required
              placeholder="Car Model"
              id="car-model"
              value={model}
              onChange={e => setModel(e.target.value)}
              type="input"
            />
          </Col>
        </Row>
      </Form.Group>
      <Form.Group>
        <div>
          <Form.Label htmlFor="car-vin">VIN Number</Form.Label>
          <Form.Control
            required
            placeholder="VIN Number"
            id="car-vin"
            value={vin}
            onChange={e => setVin(e.target.value)}
            type="input"
          />
        </div>
      </Form.Group>
      <Form.Group>
        <Row className="justify-content-md-center">
          <Col xs={6}>
            <Form.Label htmlFor="ccpg">Collector Car Price Guide Value</Form.Label>
            <Form.Control
              required
              placeholder="Collector Car Price Guide Value (CCPG)"
              id="ccpg"
              value={ccpg}
              onChange={e => setCCPG(e.target.value)}
              type="input"
            />
          </Col>
          <Col xs={6}>
            <Form.Label htmlFor="funding-goal">Funding Goal</Form.Label>
            <Form.Control
              required
              placeholder="Funding Goal"
              id="funding-goal"
              value={fundingGoal}
              onChange={e => setFundingGoal(e.target.value)}
              type="input"
            />
          </Col>
        </Row>
      </Form.Group>
      <Form.Group>
        <Row className="justify-content-md-center">
          <Col xs={6}>
            <Form.Label htmlFor="description">Description</Form.Label>
            <Form.Control
              as="textarea"
              rows="3"
              placeholder="Description"
              id="description"
              value={description}
              onChange={e => setDescription(e.target.value)}
              type="text"
            />
          </Col>
          <Col xs={6}>
            <Form.Group>
              <Form.Label htmlFor="file-upload">
                {listing ? 'Update File' : 'Upload proof of ownership'}
              </Form.Label>
              <Form.Control
                required
                ref={fileInputRef}
                id="file-upload"
                type="file"
                onChange={e => setFile(e.target.files[0])}
              />
            </Form.Group>
            {listing &&
                <div className="text-sm">
                  <div>Previously Uploaded File</div>
                  <button className="btn btn-link p-0" onClick={onFileClick}>{listing.file.fileName}</button>
                </div>
              }
          </Col>
        </Row>
      </Form.Group>
      <Form.Group>
        {!listing &&
          <div>
            <Form.Check
              required
              checked={consent}
              onChange={() => setConsent(!consent)}
              type="checkbox"
              label="I confirm to all the given data and agree to publish the project for fund listing."
            />
          </div>
        }
      </Form.Group>
      <div>
        <Button variant="primary" type="submit"
          onClick={createProjectListing}>{listing ? 'Save' : 'Submit Project'}</Button>
      </div>
    </Form>
  );
}

module.exports = ProjectListingForm;
