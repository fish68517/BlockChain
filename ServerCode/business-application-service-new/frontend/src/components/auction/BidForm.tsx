import { useState } from 'react';
import { Form, InputNumber, Button, message, Alert } from 'antd';
import { useWallet } from '@/hooks';
import { auctionService } from '@/services';

interface BidFormProps {
  listingId: number;
  currentBid?: number;
  onSuccess?: () => void;
}

export const BidForm: React.FC<BidFormProps> = ({
  listingId,
  currentBid = 0,
  onSuccess
}) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const { address, isConnected, isCorrectNetwork, connect, switchNetwork } = useWallet();

  const handleSubmit = async (values: { amount: number }) => {
    if (!address) {
      message.error('Please connect your wallet first');
      return;
    }

    if (values.amount <= currentBid) {
      message.error(`Bid must be higher than ${currentBid} ETH`);
      return;
    }

    try {
      setLoading(true);
      await auctionService.postAuction(listingId, {
        auctionPrice: values.amount,
      });
      message.success('Bid placed successfully!');
      form.resetFields();
      onSuccess?.();
    } catch (error) {
      message.error('Failed to place bid');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  if (!isConnected) {
    return (
      <Alert
        message="Wallet Not Connected"
        description="Please connect your wallet to place a bid."
        type="warning"
        action={
          <Button type="primary" onClick={connect}>
            Connect Wallet
          </Button>
        }
      />
    );
  }

  if (!isCorrectNetwork) {
    return (
      <Alert
        message="Wrong Network"
        description="Please switch to Sepolia testnet."
        type="warning"
        action={
          <Button type="primary" onClick={switchNetwork}>
            Switch Network
          </Button>
        }
      />
    );
  }

  return (
    <Form form={form} layout="vertical" onFinish={handleSubmit}>
      <Form.Item
        name="amount"
        label="Bid Amount (ETH)"
        rules={[
          { required: true, message: 'Please enter bid amount' },
          {
            type: 'number',
            min: currentBid + 0.001,
            message: `Must be higher than ${currentBid} ETH`
          },
        ]}
      >
        <InputNumber
          className="w-full"
          placeholder={`Min: ${currentBid + 0.001}`}
          step={0.01}
          precision={4}
        />
      </Form.Item>

      <Form.Item>
        <Button type="primary" htmlType="submit" loading={loading} block>
          Place Bid
        </Button>
      </Form.Item>
    </Form>
  );
};
