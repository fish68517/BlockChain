import { useState } from 'react';
import { Card, Form, Input, Button, App, Typography, Modal, Select } from 'antd';
import { UserOutlined, LockOutlined, MailOutlined, WalletOutlined, TeamOutlined } from '@ant-design/icons';
import { useNavigate, Link } from 'react-router-dom';
import api from '@/services/api';

const { Title, Text } = Typography;

interface RegisterForm {
  username: string;
  password: string;
  confirmPassword: string;
  email?: string;
  walletAddress?: string;
  role?: string;
}

export const Register: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const { message } = App.useApp();
  const navigate = useNavigate();
  const [form] = Form.useForm();

  const handleSubmit = async (values: RegisterForm) => {
    if (values.password !== values.confirmPassword) {
      message.error('Passwords do not match');
      return;
    }

    setLoading(true);
    try {
      const response: any = await api.post('/users/register', {
        username: values.username,
        password: values.password,
        email: values.email,
        walletAddress: values.walletAddress,
        role: values.role,
      });

      if (response.status === 'success') {
        // Show wallet info modal
        Modal.success({
          title: 'Registration Successful!',
          width: 600,
          content: (
            <div>
              <p><strong>Your wallet has been created:</strong></p>
              <p><strong>Address:</strong></p>
              <Input.TextArea value={response.walletAddress} readOnly rows={1} />
              <p style={{ marginTop: 16 }}><strong>Private Key (SAVE THIS!):</strong></p>
              <Input.TextArea value={response.walletPrivateKey} readOnly rows={2} />
              <p style={{ marginTop: 16, color: '#f59e0b' }}>
                ⚠️ Save your private key! You can import it into MetaMask to access your wallet.
              </p>
              <p><strong>Balance:</strong> {response.ethBalance} ETH</p>
            </div>
          ),
          onOk: () => navigate('/login'),
        });
      } else {
        message.error(response.message || 'Registration failed');
      }
    } catch (error: any) {
      message.error(error.response?.data?.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  const connectWallet = async () => {
    if (typeof window.ethereum !== 'undefined') {
      try {
        const accounts = await window.ethereum.request({ method: 'eth_requestAccounts' }) as string[];
        form.setFieldValue('walletAddress', accounts[0]);
        message.success('Wallet connected');
      } catch {
        message.error('Failed to connect wallet');
      }
    } else {
      message.error('Please install MetaMask');
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-900 p-4">
      <Card className="w-full max-w-md" style={{ background: '#1f2937' }}>
        <div className="text-center mb-6">
          <Title level={2} style={{ color: '#fff', margin: 0 }}>Create Account</Title>
          <Text type="secondary">Join CollectorCoin Platform</Text>
        </div>

        <Form form={form} layout="vertical" onFinish={handleSubmit}>
          <Form.Item
            name="username"
            rules={[
              { required: true, message: 'Please enter username' },
              { min: 3, message: 'Username must be at least 3 characters' }
            ]}
          >
            <Input prefix={<UserOutlined />} placeholder="Username" size="large" />
          </Form.Item>

          <Form.Item
            name="email"
          >
            <Input prefix={<MailOutlined />} placeholder="Email (optional)" size="large" />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[
              { required: true, message: 'Please enter password' },
              { min: 6, message: 'Password must be at least 6 characters' }
            ]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="Password" size="large" />
          </Form.Item>

          <Form.Item
            name="confirmPassword"
            rules={[{ required: true, message: 'Please confirm password' }]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="Confirm Password" size="large" />
          </Form.Item>

          <Form.Item
            name="role"
            rules={[{ required: true, message: 'Please select your role' }]}
          >
            <Select
              placeholder="Select your role"
              size="large"
              suffixIcon={<TeamOutlined />}
              options={[
                { value: 'OWNER', label: '🏠 Owner - 藏品持有者（可提交藏品）' },
                { value: 'INVESTOR', label: '💰 Investor - 投资者（可投资项目）' },
                { value: 'RESTORER', label: '🔧 Restorer - 修复师（可承接修复）' },
                { value: 'BUYER', label: '🛒 Buyer - 买家（可参与拍卖）' },
              ]}
            />
          </Form.Item>

          <Form.Item name="walletAddress">
            <Input
              prefix={<WalletOutlined />}
              placeholder="Wallet Address (optional)"
              size="large"
              addonAfter={
                <Button type="link" size="small" onClick={connectWallet}>
                  Connect
                </Button>
              }
            />
          </Form.Item>

          <Text type="secondary" className="block mb-4" style={{ fontSize: 12 }}>
            * Connecting wallet will allocate 1000 ETH to your account for testing
          </Text>

          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading} block size="large">
              Register
            </Button>
          </Form.Item>

          <div className="text-center">
            <Text type="secondary">Already have an account? </Text>
            <Link to="/login">Login</Link>
          </div>
        </Form>
      </Card>
    </div>
  );
};
