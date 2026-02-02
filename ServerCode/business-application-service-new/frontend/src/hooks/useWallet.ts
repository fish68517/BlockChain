import { useEffect, useCallback } from 'react';
import { useAppDispatch, useAppSelector } from './useStore';
import {
  connectWallet,
  disconnect,
  setChainId,
  setAddress,
  selectIsCorrectNetwork,
} from '@/store/slices/walletSlice';
import { switchToSepolia, isMetaMaskInstalled } from '@/utils/web3';

export const useWallet = () => {
  const dispatch = useAppDispatch();
  const { address, balance, chainId, isConnecting, error } = useAppSelector(
    (state) => state.wallet
  );
  const isCorrectNetwork = useAppSelector(selectIsCorrectNetwork);

  const connect = useCallback(() => {
    dispatch(connectWallet());
  }, [dispatch]);

  const disconnectWallet = useCallback(() => {
    dispatch(disconnect());
  }, [dispatch]);

  const switchNetwork = useCallback(async () => {
    await switchToSepolia();
  }, []);

  useEffect(() => {
    if (!window.ethereum) return;

    const handleAccountsChanged = (accounts: unknown) => {
      const accs = accounts as string[];
      if (accs.length === 0) {
        dispatch(disconnect());
      } else {
        dispatch(setAddress(accs[0]));
      }
    };

    const handleChainChanged = (chainId: unknown) => {
      dispatch(setChainId(chainId as string));
    };

    window.ethereum.on('accountsChanged', handleAccountsChanged);
    window.ethereum.on('chainChanged', handleChainChanged);

    return () => {
      window.ethereum?.removeListener('accountsChanged', handleAccountsChanged);
      window.ethereum?.removeListener('chainChanged', handleChainChanged);
    };
  }, [dispatch]);

  return {
    address,
    balance,
    chainId,
    isConnecting,
    error,
    isConnected: !!address,
    isCorrectNetwork,
    isMetaMaskInstalled: isMetaMaskInstalled(),
    connect,
    disconnect: disconnectWallet,
    switchNetwork,
  };
};
