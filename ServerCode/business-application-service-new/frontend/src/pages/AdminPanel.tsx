import { useEffect, useState } from 'react';
import { Card, Table, Button, Tag, Space, Modal, Form, InputNumber, Input, App } from 'antd';
import { Typography } from 'antd';
import { listingService, auctionService } from '@/services';
import type { ProjectListing, EstimateRequest } from '@/types';

const { Title } = Typography;

export const AdminPanel: React.FC = () => {
  const [listings, setListings] = useState<ProjectListing[]>([]);
  const [loading, setLoading] = useState(false);
  const [estimateModal, setEstimateModal] = useState(false);
  const [auctionModal, setAuctionModal] = useState(false);
  const [finalizeModal, setFinalizeModal] = useState(false);
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [form] = Form.useForm();
  const [auctionForm] = Form.useForm();
  const [finalizeForm] = Form.useForm();
  const { message } = App.useApp();

  const fetchListings = async () => {
    setLoading(true);
    try {
      const data = await listingService.getAll();
      setListings(data as unknown as ProjectListing[]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchListings();
  }, []);

  const handleVerify = async (id: number) => {
    try {
      await listingService.verify(id);
      message.success('Project verified');
      fetchListings();
    } catch {
      message.error('Failed to verify');
    }
  };

  const handleEstimate = async (values: EstimateRequest) => {
    if (!selectedId) return;
    try {
      await listingService.estimate(selectedId, values);
      message.success('Estimation saved and NFT minted');
      setEstimateModal(false);
      form.resetFields();
      fetchListings();
    } catch {
      message.error('Failed to estimate');
    }
  };

  const handleLaunch = async (id: number) => {
    try {
      await listingService.launch(id);
      message.success('Project launched');
      fetchListings();
    } catch {
      message.error('Failed to launch');
    }
  };

  const handleStartAuction = async (values: { auctionPrice: number }) => {
    if (!selectedId) return;
    try {
      await auctionService.postAuction(selectedId, { auctionPrice: values.auctionPrice });
      message.success('Auction started');
      setAuctionModal(false);
      auctionForm.resetFields();
      fetchListings();
    } catch {
      message.error('Failed to start auction');
    }
  };

  const handleFinalizeAuction = async (values: { winnerAddress: string }) => {
    if (!selectedId) return;
    try {
      await auctionService.finalize(selectedId, { winnerAddress: values.winnerAddress });
      message.success('Auction finalized! NFT transferred to winner.');
      setFinalizeModal(false);
      finalizeForm.resetFields();
      fetchListings();
    } catch {
      message.error('Failed to finalize auction');
    }
  };

  const getStatusColor = (status: string) => {
    const colors: Record<string, string> = {
      PENDING: 'orange',
      VERIFIED: 'blue',
      ESTIMATED: 'cyan',
      LAUNCHED: 'green',
      FUNDING: 'purple',
      RESTORING: 'gold',
      AUCTION: 'magenta',
      SOLD: 'green',
      REJECTED: 'red',
    };
    return colors[status] || 'default';
  };

  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
    { title: 'Title', dataIndex: 'title', key: 'title' },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => <Tag color={getStatusColor(status)}>{status}</Tag>,
    },
    {
      title: 'NFT',
      key: 'nft',
      render: (_: unknown, record: ProjectListing) => (
        <Space>
          {record.nftMinted && <Tag color="green">Minted</Tag>}
          {record.nftLaunched && <Tag color="blue">Launched</Tag>}
        </Space>
      ),
    },
    {
      title: 'Actions',
      key: 'actions',
      render: (_: unknown, record: ProjectListing) => (
        <Space>
          {record.status === 'PENDING' && (
            <Button size="small" onClick={() => handleVerify(record.id)}>
              Verify
            </Button>
          )}
          {record.status === 'VERIFIED' && (
            <Button
              size="small"
              type="primary"
              onClick={() => {
                setSelectedId(record.id);
                setEstimateModal(true);
              }}
            >
              Estimate
            </Button>
          )}
          {record.status === 'ESTIMATED' && !record.nftLaunched && (
            <Button size="small" onClick={() => handleLaunch(record.id)}>
              Launch
            </Button>
          )}
          {record.status === 'RESTORED' && (
            <Button 
              size="small" 
              type="primary"
              onClick={() => {
                setSelectedId(record.id);
                setAuctionModal(true);
              }}
            >
              Start Auction
            </Button>
          )}
          {record.status === 'AUCTION' && (
            <Button 
              size="small" 
              type="primary"
              style={{ backgroundColor: '#52c41a' }}
              onClick={() => {
                setSelectedId(record.id);
                setFinalizeModal(true);
              }}
            >
              Finalize
            </Button>
          )}
        </Space>
      ),
    },
  ];

  return (
    <div className="p-6">
      <Title level={2}>Admin Panel</Title>
      <Card className="mt-4">
        <Table
          columns={columns}
          dataSource={listings}
          rowKey="id"
          loading={loading}
          pagination={{ pageSize: 10 }}
        />
      </Card>

      <Modal
        title="Value Estimation"
        open={estimateModal}
        onCancel={() => setEstimateModal(false)}
        onOk={() => form.submit()}
      >
        <Form form={form} layout="vertical" onFinish={handleEstimate}>
          <Form.Item
            name="valueEstimation"
            label="Value (ETH)"
            rules={[{ required: true }]}
          >
            <InputNumber min={0} step={0.01} className="w-full" />
          </Form.Item>
          <Form.Item
            name="repairEstimation"
            label="Repair Cost (ETH)"
            rules={[{ required: true }]}
          >
            <InputNumber min={0} step={0.01} className="w-full" />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="Start Auction"
        open={auctionModal}
        onCancel={() => setAuctionModal(false)}
        onOk={() => auctionForm.submit()}
      >
        <Form form={auctionForm} layout="vertical" onFinish={handleStartAuction}>
          <Form.Item
            name="auctionPrice"
            label="Auction Price (ETH)"
            rules={[{ required: true, message: 'Please enter auction price' }]}
          >
            <InputNumber min={0} step={0.01} className="w-full" placeholder="2.0" />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="Finalize Auction"
        open={finalizeModal}
        onCancel={() => setFinalizeModal(false)}
        onOk={() => finalizeForm.submit()}
      >
        <Form form={finalizeForm} layout="vertical" onFinish={handleFinalizeAuction}>
          <Form.Item
            name="winnerAddress"
            label="Winner Wallet Address"
            rules={[{ required: true, message: 'Please enter winner address' }]}
          >
            <Input placeholder="0x..." />
          </Form.Item>
          <p style={{ color: '#666', fontSize: 12 }}>
            This will transfer the NFT to the winner and distribute funds to investors.
          </p>
        </Form>
      </Modal>
    </div>
  );
};
