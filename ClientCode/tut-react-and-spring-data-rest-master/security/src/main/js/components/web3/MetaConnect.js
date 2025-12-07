const React = require('react');
const { useState } = require('react');
const { Button } = require('react-bootstrap');
const { getCCTokenBalance } = require('../../utils/web3Utils');
const MetaConnectModal = require('./MetaConnectModal');

const MetaConnect = () => {
    const [connected, setConnected] = useState(false);
    const [userBalance, setUserBalance] = useState(null);
    const [showModal, setShowModal] = useState(false);

    const connectWalletHandler = () => {
        if(window.ethereum) {
            initialiseConnection();
        } else {
            setShowModal(true);
        }
    }

    const initialiseConnection = async () => {
        try {
            setUserBalance(await getCCTokenBalance());
            setConnected(true);
        } catch(ex) {
            console.log(ex);
        }
    }

    const ChangedHandler = () => {
        window.location.reload();
    }

    if(window.ethereum) {
        window.ethereum.on('accountsChanged', ChangedHandler);
        window.ethereum.on('chainChanged', ChangedHandler);
    }

    return (
        <>
            <MetaConnectModal showModal={showModal} setShowModal={setShowModal}/>
            {(connected) ?
            <Button className="btn btn-light" href="https://app.uniswap.org/swap" target="_blank">Balance: {userBalance}</Button>
            : 
            <Button className="btn btn-light" onClick={connectWalletHandler}>Connect Wallet</Button>
            }
        </>
    );
}

module.exports = MetaConnect;