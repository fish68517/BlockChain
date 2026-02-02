import { useState, useEffect } from 'react';
import { Card, Table, Tag, Button, Space, Typography, Statistic, Row, Col, Empty } from 'antd';
import { 
  ShoppingCartOutlined, 
  EyeOutlined, 
  TrophyOutlined,
  DollarOutlined,
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { auctionApi } from '@/services/api';

const { Title, Text } = Typography;

interface Purchase {
  id: number;
  listingId: number;
  buyerId: number;
  buyerAddress: string;
  bidAmount: number;
  isSelected: boolean;
  isProcessed: boolean;
  transactionHash?: string;
  createdAt: string;
}

export const MyPurchases: React.FC = () => {
  const [purchases, setPurchases] = useState<Purchase[]>([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const userId = localStorage.getItem('userId');

  useEffect(() => {
    if (userId) {
      fetchMyPurchases();
    }
  }, [userId]);

  const fetchMyPurchases = async () => {
    try {
      const response: any = await auctionApi.getBidsByBuyer(Number(userId));
      // Filter to show only selected/won bids as purchases
      const wonBids = (response || []).filter((bid: Purchase) => bid.isSelected);
      setPurchases(wonBids);
    } catch (error) {
      console.error('Failed to fetch purchases:', error);
    } finally {
      setLoading(false);
    }
  };

  const columns = [
    {
      title: 'Listing ID',
      dataIndex: 'listingId',
      key: 'listingId',
      render: (id: number) => (
        <Space>
          <TrophyOutlined style={{ color: '#f59e0b' }} />
          <Text strong>#{id}</Text>
        </Space>
      ),
    },
    {
      title: 'Bid Amount',
      dataIndex: 'bidAmount',
      key: 'bidAmount',
      render: (amount: number) => <Text strong>{amount} ETH</Text>,
    },
    {
      title: 'Status',
      key: 'status',
      render: (_: any, record: Purchase) => {
        if (record.isProcessed) {
          return <Tag color="green">COMPLETED</Tag>;
        } else if (record.isSelected) {
          return <Tag color="blue">WON</Tag>;
        }
        return <Tag color="orange">PENDING</Tag>;
      },
    },
    {
      title: 'Transaction',
      dataIndex: 'transactionHash',
      key: 'transactionHash',
      render: (hash: string) => hash ? 
        <Text code style={{ fontSize: 12 }}>{hash.slice(0, 10)}...</Text> : '-',
    },
    {
      title: 'Date',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (date: string) => date ? new Date(date).toLocaleDateString() : '-',
    },
    {
      title: 'Actions',
      key: 'actions',
      render: (_: any, record: Purchase) => (
        <Button 
          icon={<EyeOutlined />} 
          onClick={() => navigate(`/listings/${record.listingId}`)}
        >
          View
        </Button>
      ),
    },
  ];

  const totalSpent = purchases.reduce((sum, p) => sum + (p.bidAmount || 0), 0);
  const completedCount = purchases.filter(p => p.isProcessed).length;

  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>
        <ShoppingCartOutlined style={{ marginRight: 12, color: '#3b82f6' }} />
        My Purchases
      </Title>

      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={8}>
          <Card>
            <Statistic
              title="Total Purchases"
              value={purchases.length}
              prefix={<ShoppingCartOutlined />}
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card>
            <Statistic
              title="Completed"
              value={completedCount}
              valueStyle={{ color: '#10b981' }}
              prefix={<TrophyOutlined />}
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card>
            <Statistic
              title="Total Spent"
              value={totalSpent}
              precision={2}
              suffix="ETH"
              valueStyle={{ color: '#f59e0b' }}
              prefix={<DollarOutlined />}
            />
          </Card>
        </Col>
      </Row>

      <Card>
        {purchases.length > 0 ? (
          <Table
            columns={columns}
            dataSource={purchases}
            rowKey="id"
            loading={loading}
            pagination={{ pageSize: 10 }}
          />
        ) : (
          <Empty
            description="You haven't made any purchases yet"
            image={Empty.PRESENTED_IMAGE_SIMPLE}
          >
            <Button type="primary" onClick={() => navigate('/listings')}>
              Browse Listings
            </Button>
          </Empty>
        )}
      </Card>
    </div>
  );
};
