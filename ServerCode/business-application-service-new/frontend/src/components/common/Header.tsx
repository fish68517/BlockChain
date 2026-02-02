import { Layout, Space, Tag, Button, Typography } from 'antd';
import { ThunderboltOutlined, GlobalOutlined, LogoutOutlined, UserOutlined } from '@ant-design/icons';
import { ConnectWallet } from '@/components/wallet';
import { useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';

const { Header: AntHeader } = Layout;
const { Text } = Typography;

export const Header: React.FC = () => {
  const navigate = useNavigate();
  const [username, setUsername] = useState<string | null>(null);

  useEffect(() => {
    setUsername(localStorage.getItem('username'));
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    localStorage.removeItem('username');
    localStorage.removeItem('role');
    localStorage.removeItem('walletAddress');
    localStorage.removeItem('ethBalance');
    setUsername(null);
    navigate('/login');
  };

  const role = localStorage.getItem('role');
  const isAdmin = role === 'ADMIN';

  return (
    <AntHeader style={{
      background: '#ffffff',
      borderBottom: '1px solid #e2e8f0',
      padding: '0 24px',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      height: 64,
      boxShadow: '0 1px 3px rgba(0,0,0,0.05)',
    }}>
      {/* Left side - Network status */}
      <Space size="middle">
        <Tag 
          icon={<GlobalOutlined />} 
          color="processing"
          style={{ margin: 0, borderRadius: 6 }}
        >
          Localhost:8545
        </Tag>
        <Tag 
          icon={<ThunderboltOutlined />} 
          style={{ 
            background: 'rgba(245, 158, 11, 0.1)', 
            border: '1px solid #f59e0b',
            color: '#d97706',
            margin: 0,
            borderRadius: 6,
          }}
        >
          Gas: ~15 Gwei
        </Tag>
      </Space>

      {/* Right side - User info and Wallet connection */}
      <Space size="middle">
        {username && (
          <>
            <Text style={{ color: '#64748b' }}>
              <UserOutlined /> {username}
              {isAdmin && <Tag color="gold" style={{ marginLeft: 8 }}>ADMIN</Tag>}
            </Text>
            <Button 
              type="text" 
              icon={<LogoutOutlined />} 
              onClick={handleLogout}
              style={{ color: '#64748b' }}
            >
              Logout
            </Button>
          </>
        )}
        <ConnectWallet />
      </Space>
    </AntHeader>
  );
};
