import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { getProvider, SEPOLIA_CHAIN_ID } from '@/utils/web3';

interface WalletState {
  address: string | null;
  balance: string | null;
  chainId: string | null;
  isConnecting: boolean;
  error: string | null;
}

const initialState: WalletState = {
  address: null,
  balance: null,
  chainId: null,
  isConnecting: false,
  error: null,
};

export const connectWallet = createAsyncThunk(
  'wallet/connect',
  async () => {
    if (!window.ethereum) {
      throw new Error('MetaMask not installed');
    }
    const accounts = await window.ethereum.request({
      method: 'eth_requestAccounts',
    }) as string[];

    const chainId = await window.ethereum.request({
      method: 'eth_chainId',
    }) as string;

    const provider = getProvider();
    const balance = provider
      ? await provider.getBalance(accounts[0])
      : null;

    return {
      address: accounts[0],
      chainId,
      balance: balance?.toString() || '0',
    };
  }
);

const walletSlice = createSlice({
  name: 'wallet',
  initialState,
  reducers: {
    disconnect: (state) => {
      state.address = null;
      state.balance = null;
      state.chainId = null;
    },
    setChainId: (state, action) => {
      state.chainId = action.payload;
    },
    setAddress: (state, action) => {
      state.address = action.payload;
    },
    deductBalance: (state, action) => {
      if (state.balance) {
        const currentBalance = BigInt(state.balance);
        const deduction = BigInt(Math.floor(action.payload * 1e18));
        const newBalance = currentBalance - deduction;
        state.balance = newBalance > 0n ? newBalance.toString() : '0';
      }
    },
    setBalance: (state, action) => {
      state.balance = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(connectWallet.pending, (state) => {
        state.isConnecting = true;
        state.error = null;
      })
      .addCase(connectWallet.fulfilled, (state, action) => {
        state.isConnecting = false;
        state.address = action.payload.address;
        state.chainId = action.payload.chainId;
        state.balance = action.payload.balance;
      })
      .addCase(connectWallet.rejected, (state, action) => {
        state.isConnecting = false;
        state.error = action.error.message || 'Connection failed';
      });
  },
});

export const { disconnect, setChainId, setAddress, deductBalance, setBalance } = walletSlice.actions;
export const selectIsCorrectNetwork = (state: { wallet: WalletState }) =>
  state.wallet.chainId === SEPOLIA_CHAIN_ID;

export default walletSlice.reducer;
