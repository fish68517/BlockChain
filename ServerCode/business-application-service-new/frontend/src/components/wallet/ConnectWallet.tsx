import { Button, message } from 'antd';
import { WalletOutlined } from '@ant-design/icons';
import { useWallet } from '@/hooks/useWallet';
import { formatAddress } from '@/utils/web3';

export const ConnectWallet: React.FC = () => {
  const {
    address,
    isConnecting,
    isConnected,
    isCorrectNetwork,
    isMetaMaskInstalled,
    connect,
    disconnect,
    switchNetwork,
  } = useWallet();

  const handleClick = async () => {
    if (!isMetaMaskInstalled) {
      message.error('Please install MetaMask');
      return;
    }

    if (isConnected) {
      disconnect();
    } else {
      connect();
    }
  };

  if (isConnected && !isCorrectNetwork) {
    return (
      <Button type="primary" danger onClick={switchNetwork}>
        Switch to Sepolia
      </Button>
    );
  }

  return (
    <Button
      type="primary"
      icon={<WalletOutlined />}
      loading={isConnecting}
      onClick={handleClick}
    >
      {isConnected ? formatAddress(address!) : 'Connect Wallet'}
    </Button>
  );
};
