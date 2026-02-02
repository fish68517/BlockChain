import { useState, useEffect } from 'react';
import { Card, Table, Tag, Button, Space, Typography, Statistic, Row, Col, Empty } from 'antd';
import { 
  CrownOutlined, 
  EyeOutlined, 
  DollarOutlined,
  RiseOutlined,
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import api from '@/services/api';

const { Title, Text } = Typography;

interface Item {
  id: number;
  title: string;
  description: string;
  status: string;
  price: number;
  tokenId?: number;
  imageUrl?: string;
  createdAt: string;
}

export const MyItems: React.FC = () => {
  const [items, setItems] = useState<Item[]>([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const walletAddress = localStorage.getItem('walletAddress');

  useEffect(() => {
    fetchMyItems();
  }, []);

  const fetchMyItems = async () => {
    try {
      const response: any = await api.get('/listings', {
        params: { owner: walletAddress }
      });
      setItems(response.data || response || []);
    } catch (error) {
      console.error('Failed to fetch items:', error);
    } finally {
      setLoading(false);
    }
  };

  const columns = [
    {
      title: 'Item',
      dataIndex: 'title',
      key: 'title',
      render: (text: string) => (
        <Space>
          <CrownOutlined style={{ color: '#f59e0b' }} />
          <Text strong>{text}</Text>
        </Space>
      ),
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => {
        const colors: Record<string, string> = {
          PENDING: 'orange',
          LISTED: 'blue',
          SOLD: 'green',
          RESTORED: 'purple',
        };
        return <Tag color={colors[status] || 'default'}>{status}</Tag>;
      },
    },
    {
      title: 'Price',
      dataIndex: 'price',
      key: 'price',
      render: (price: number) => <Text strong>{price} ETH</Text>,
    },
    {
      title: 'Token ID',
      dataIndex: 'tokenId',
      key: 'tokenId',
      render: (tokenId: number) => tokenId ? `#${tokenId}` : '-',
    },
    {
      title: 'Actions',
      key: 'actions',
      render: (_: any, record: Item) => (
        <Space>
          <Button 
            icon={<EyeOutlined />} 
            onClick={() => navigate(`/listings/${record.id}`)}
          >
            View
          </Button>
        </Space>
      ),
    },
  ];

  const totalValue = items.reduce((sum, item) => sum + (item.price || 0), 0);
  const listedCount = items.filter(item => item.status === 'LISTED').length;

  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>
        <CrownOutlined style={{ marginRight: 12, color: '#f59e0b' }} />
        My Items
      </Title>

      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={8}>
          <Card>
            <Statistic
              title="Total Items"
              value={items.length}
              prefix={<CrownOutlined />}
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card>
            <Statistic
              title="Listed Items"
              value={listedCount}
              valueStyle={{ color: '#3b82f6' }}
              prefix={<RiseOutlined />}
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card>
            <Statistic
              title="Total Value"
              value={totalValue}
              precision={2}
              suffix="ETH"
              valueStyle={{ color: '#10b981' }}
              prefix={<DollarOutlined />}
            />
          </Card>
        </Col>
      </Row>

      <Card>
        {items.length > 0 ? (
          <Table
            columns={columns}
            dataSource={items}
            rowKey="id"
            loading={loading}
            pagination={{ pageSize: 10 }}
          />
        ) : (
          <Empty
            description="You don't have any items yet"
            image={Empty.PRESENTED_IMAGE_SIMPLE}
          >
            <Button type="primary" onClick={() => navigate('/create')}>
              Create New Listing
            </Button>
          </Empty>
        )}
      </Card>
    </div>
  );
};
