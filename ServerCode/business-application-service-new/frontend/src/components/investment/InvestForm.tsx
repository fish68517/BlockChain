import { useState } from 'react';
import { Form, InputNumber, Button, Alert, App } from 'antd';
import { useWallet } from '@/hooks';
import { investmentService } from '@/services';
import { useDispatch } from 'react-redux';
import { deductBalance } from '@/store/slices/walletSlice';

interface InvestFormProps {
  listingId: number;
  onSuccess?: () => void;
}

export const InvestForm: React.FC<InvestFormProps> = ({
  listingId,
  onSuccess
}) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const { address, isConnected, connect } = useWallet();
  const { message } = App.useApp();
  const dispatch = useDispatch();

  const handleSubmit = async (values: { amount: number }) => {
    if (!address) {
      message.error('Please connect your wallet first');
      return;
    }

    try {
      setLoading(true);
      await investmentService.invest(listingId, {
        investorAddress: address,
        amount: values.amount,
      });
      // Deduct balance from wallet display (simulation)
      dispatch(deductBalance(values.amount));
      message.success(`Investment of ${values.amount} ETH successful! Balance updated.`);
      form.resetFields();
      onSuccess?.();
    } catch (error) {
      message.error('Investment failed');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  if (!isConnected) {
    return (
      <Alert
        type="warning"
        showIcon
        message={
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <strong>Wallet Not Connected</strong>
              <p style={{ margin: 0 }}>Please connect your wallet to invest.</p>
            </div>
            <Button type="primary" onClick={connect}>
              Connect Wallet
            </Button>
          </div>
        }
      />
    );
  }

  return (
    <Form form={form} layout="vertical" onFinish={handleSubmit}>
      <Form.Item
        name="amount"
        label="Investment Amount (ETH)"
        rules={[
          { required: true, message: 'Please enter amount' },
          { type: 'number', min: 0.001, message: 'Minimum 0.001 ETH' },
        ]}
      >
        <InputNumber
          className="w-full"
          placeholder="0.00"
          step={0.01}
          precision={4}
        />
      </Form.Item>

      <Form.Item>
        <Button
          type="primary"
          htmlType="submit"
          loading={loading}
          block
        >
          Invest Now
        </Button>
      </Form.Item>
    </Form>
  );
};
