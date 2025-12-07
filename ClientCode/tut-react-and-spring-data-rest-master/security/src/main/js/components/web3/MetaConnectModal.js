const React = require('react');
const { Modal, Button } = require('react-bootstrap');

const MetaConnectModal = (props) => {
    const closeModalHandler = () => {
        props.setShowModal(false);
    }

    return (
        <Modal show={props.showModal} onHide={closeModalHandler}>
            <Modal.Header closeButton>
            <Modal.Title>Connection Error</Modal.Title>
            </Modal.Header>
            <Modal.Body>You do not have an active Metamask wallet to connect to. Create a new wallet using MetaMask or link your existing wallet to MetaMask to proceed further.</Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={closeModalHandler}>
                    Close
                </Button>
                <Button variant="primary" onClick={closeModalHandler} href="https://metamask.io/" target='_blank'>
                    MetaMask.io
                </Button>
            </Modal.Footer>
        </Modal>
    );
}

module.exports = MetaConnectModal;