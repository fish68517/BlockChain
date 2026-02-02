import { useEffect, useState } from 'react';
import { Card, Table, Button, Tag, Typography, Modal, Input, App } from 'antd';
import { listingService, investmentService } from '@/services';
import type { ProjectListing } from '@/types';

const { Title } = Typography;

export const RestorerPanel: React.FC = () => {
  const [listings, setListings] = useState<ProjectListing[]>([]);
  const [loading, setLoading] = useState(false);
  const [assignModalVisible, setAssignModalVisible] = useState(false);
  const [selectedListing, setSelectedListing] = useState<ProjectListing | null>(null);
  const [restorerAddress, setRestorerAddress] = useState('');
  const { message } = App.useApp();

  const fetchListings = async () => {
    setLoading(true);
    try {
      const data = await listingService.getAll();
      const filtered = (data as unknown as ProjectListing[])
        .filter(l => l.status === 'FUNDED' || l.status === 'RESTORING');
      setListings(filtered);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchListings();
  }, []);

  const handleAssignRestorer = async () => {
    if (!selectedListing || !restorerAddress) return;
    try {
      await investmentService.assignRestorer(selectedListing.id, { restorerAddress });
      message.success('Restorer assigned successfully');
      setAssignModalVisible(false);
      setRestorerAddress('');
      fetchListings();
    } catch {
      message.error('Failed to assign restorer');
    }
  };

  const handleMarkComplete = async (listing: ProjectListing) => {
    try {
      await investmentService.completeRestoration(listing.id);
      message.success('Restoration marked complete');
      fetchListings();
    } catch {
      message.error('Failed to mark complete');
    }
  };

  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
    { title: 'Title', dataIndex: 'title', key: 'title' },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => (
        <Tag color={status === 'FUNDED' ? 'green' : 'gold'}>{status}</Tag>
      ),
    },
    {
      title: 'Actions',
      key: 'actions',
      render: (_: unknown, record: ProjectListing) => (
        <>
          {record.status === 'FUNDED' && (
            <Button 
              size="small" 
              type="primary"
              onClick={() => {
                setSelectedListing(record);
                setAssignModalVisible(true);
              }}
            >
              Assign Restorer
            </Button>
          )}
          {record.status === 'RESTORING' && (
            <Button 
              size="small" 
              type="primary"
              onClick={() => handleMarkComplete(record)}
            >
              Mark Complete
            </Button>
          )}
        </>
      ),
    },
  ];

  return (
    <div className="p-6">
      <Title level={2}>Restorer Panel</Title>
      <Card className="mt-4">
        <Table
          columns={columns}
          dataSource={listings}
          rowKey="id"
          loading={loading}
        />
      </Card>

      <Modal
        title="Assign Restorer"
        open={assignModalVisible}
        onOk={handleAssignRestorer}
        onCancel={() => setAssignModalVisible(false)}
      >
        <p>Enter restorer wallet address:</p>
        <Input
          placeholder="0x..."
          value={restorerAddress}
          onChange={(e) => setRestorerAddress(e.target.value)}
        />
      </Modal>
    </div>
  );
};
