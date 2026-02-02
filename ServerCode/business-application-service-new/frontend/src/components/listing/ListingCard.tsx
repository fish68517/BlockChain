import { Card, Tag, Typography, Image, Button, Popconfirm, App } from 'antd';
import { PictureOutlined, DeleteOutlined } from '@ant-design/icons';
import { Link } from 'react-router-dom';
import { listingService } from '@/services';
import type { ProjectListing, ListingStatus } from '@/types';

const { Text, Title } = Typography;

interface ListingCardProps {
  listing: ProjectListing;
  onDelete?: () => void;
}

const statusColors: Record<ListingStatus, string> = {
  PENDING: 'default',
  VERIFIED: 'processing',
  ESTIMATED: 'cyan',
  LAUNCHED: 'geekblue',
  FUNDING: 'warning',
  FUNDED: 'success',
  RESTORING: 'purple',
  RESTORED: 'magenta',
  AUCTION: 'gold',
  SOLD: 'lime',
  REJECTED: 'error',
};

export const ListingCard: React.FC<ListingCardProps> = ({ listing, onDelete }) => {
  const hasImage = listing.imageUrl && listing.imageUrl.trim() !== '';
  const { message } = App.useApp();

  const handleDelete = async () => {
    try {
      await listingService.delete(listing.id);
      message.success('Project deleted successfully');
      onDelete?.();
    } catch {
      message.error('Failed to delete project');
    }
  };

  return (
    <Link to={`/listings/${listing.id}`}>
      <Card
        hoverable
        className="h-full"
        style={{ 
          borderRadius: 12, 
          overflow: 'hidden',
          border: '1px solid #e2e8f0',
        }}
        cover={
          hasImage ? (
            <div style={{ height: 160, overflow: 'hidden' }}>
              <Image
                src={listing.imageUrl}
                alt={listing.title}
                style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                preview={false}
                fallback="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='100' height='100' viewBox='0 0 100 100'%3E%3Crect fill='%23f1f5f9' width='100' height='100'/%3E%3Ctext x='50' y='55' font-size='12' text-anchor='middle' fill='%2394a3b8'%3ENo Image%3C/text%3E%3C/svg%3E"
              />
            </div>
          ) : (
            <div 
              style={{ 
                height: 160, 
                background: 'linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%)',
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                justifyContent: 'center',
              }}
            >
              <PictureOutlined style={{ fontSize: 32, color: '#94a3b8' }} />
              <Text type="secondary" style={{ marginTop: 8 }}>No Image</Text>
            </div>
          )
        }
      >
        <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
            <Title level={5} className="!mb-0 truncate" style={{ flex: 1 }}>
              {listing.title}
            </Title>
            <Popconfirm
              title="Delete this project?"
              description="This action cannot be undone."
              onConfirm={handleDelete}
              okText="Delete"
              cancelText="Cancel"
              okButtonProps={{ danger: true }}
            >
              <Button
                type="text"
                danger
                size="small"
                icon={<DeleteOutlined />}
                onClick={(e) => e.preventDefault()}
                style={{ marginLeft: 8 }}
              />
            </Popconfirm>
          </div>
          <Tag color={statusColors[listing.status]}>
            {listing.status}
          </Tag>
          {listing.valueEstimation && (
            <Text type="secondary">
              Est. Value: {listing.valueEstimation} ETH
            </Text>
          )}
          {listing.nftTokenId && (
            <Text type="secondary" className="text-xs">
              Token ID: #{listing.nftTokenId}
            </Text>
          )}
        </div>
      </Card>
    </Link>
  );
};
