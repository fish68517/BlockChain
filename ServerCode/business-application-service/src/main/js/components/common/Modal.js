const React = require("react");
const { useState } = require("react");
const { Modal } = require("react-bootstrap");

const ModalWithBtn = ({
  modalHeading,
  modalBody,
  btnText,
  btnClassname,
  onSubmit,
}) => {
  const [show, setShow] = useState(false);

  const handleSubmit = () => {
    onSubmit();
    handleClose();
  };

  const handleClose = () => setShow(false);
  const handleShow = () => setShow(true);

  return (
    <>
      <button className={btnClassname} onClick={handleShow}>
        {btnText}
      </button>

      <Modal show={show} onHide={handleClose}>
        <Modal.Header closeButton>
          <Modal.Title>{modalHeading}</Modal.Title>
        </Modal.Header>
        <Modal.Body>{modalBody}</Modal.Body>
        <Modal.Footer>
          <button className="btn btn-secondary" onClick={handleClose}>
            Close
          </button>
          <button className="btn btn-primary" onClick={handleSubmit}>
            Confirm
          </button>
        </Modal.Footer>
      </Modal>
    </>
  );
};

module.exports = ModalWithBtn;
