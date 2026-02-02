import { useState } from 'react';
import { Card, Form, Input, Button, App, Typography } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { useNavigate, Link } from 'react-router-dom';
import api from '@/services/api';

const { Title, Text } = Typography;

interface LoginForm {
  username: string;
  password: string;
}

export const Login: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const { message } = App.useApp();
  const navigate = useNavigate();

  const handleSubmit = async (values: LoginForm) => {
    setLoading(true);
    try {
      const response: any = await api.post('/users/login', values);

      if (response.status === 'success') {
        localStorage.setItem('token', response.token);
        localStorage.setItem('userId', response.userId);
        localStorage.setItem('username', response.username);
        localStorage.setItem('role', response.role);
        localStorage.setItem('walletAddress', response.walletAddress || '');
        localStorage.setItem('ethBalance', response.ethBalance || '0');
        message.success('Login successful!');
        navigate('/');
      } else {
        message.error(response.message || 'Login failed');
      }
    } catch (error: any) {
      message.error(error.response?.data?.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-900 p-4">
      <Card className="w-full max-w-md" style={{ background: '#1f2937' }}>
        <div className="text-center mb-6">
          <Title level={2} style={{ color: '#fff', margin: 0 }}>Welcome Back</Title>
          <Text type="secondary">Login to CollectorCoin</Text>
        </div>

        <Form layout="vertical" onFinish={handleSubmit}>
          <Form.Item
            name="username"
            rules={[{ required: true, message: 'Please enter username' }]}
          >
            <Input prefix={<UserOutlined />} placeholder="Username" size="large" />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[{ required: true, message: 'Please enter password' }]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="Password" size="large" />
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading} block size="large">
              Login
            </Button>
          </Form.Item>

          <div className="text-center">
            <Text type="secondary">Don't have an account? </Text>
            <Link to="/register">Register</Link>
          </div>
        </Form>
      </Card>
    </div>
  );
};
