import { BrowserProvider, JsonRpcSigner } from 'ethers';

declare global {
  interface Window {
    ethereum?: {
      isMetaMask?: boolean;
      request: (args: { method: string; params?: unknown[] }) => Promise<unknown>;
      on: (event: string, callback: (...args: unknown[]) => void) => void;
      removeListener: (event: string, callback: (...args: unknown[]) => void) => void;
    };
  }
}

export const SEPOLIA_CHAIN_ID = '0xaa36a7'; // 11155111

export const isMetaMaskInstalled = (): boolean => {
  return typeof window !== 'undefined' && !!window.ethereum?.isMetaMask;
};

export const getProvider = (): BrowserProvider | null => {
  if (!isMetaMaskInstalled()) return null;
  return new BrowserProvider(window.ethereum!);
};

export const getSigner = async (): Promise<JsonRpcSigner | null> => {
  const provider = getProvider();
  if (!provider) return null;
  return provider.getSigner();
};

export const formatAddress = (address: string): string => {
  return `${address.slice(0, 6)}...${address.slice(-4)}`;
};

export const switchToSepolia = async (): Promise<boolean> => {
  if (!window.ethereum) return false;
  try {
    await window.ethereum.request({
      method: 'wallet_switchEthereumChain',
      params: [{ chainId: SEPOLIA_CHAIN_ID }],
    });
    return true;
  } catch (error) {
    console.error('Failed to switch network:', error);
    return false;
  }
};
