import { Card, Descriptions, Tag, Space, Typography } from 'antd';
import type { ProjectListing, ListingStatus } from '@/types';

const { Title, Text } = Typography;

interface ListingDetailProps {
  listing: ProjectListing;
}

const statusColors: Record<ListingStatus, string> = {
  PENDING: 'default',
  VERIFIED: 'blue',
  ESTIMATED: 'cyan',
  LAUNCHED: 'geekblue',
  FUNDING: 'orange',
  FUNDED: 'green',
  RESTORING: 'purple',
  RESTORED: 'magenta',
  AUCTION: 'gold',
  SOLD: 'lime',
  REJECTED: 'red',
};

export const ListingDetail: React.FC<ListingDetailProps> = ({ listing }) => {
  return (
    <Space direction="vertical" size="large" className="w-full">
      <Card>
        <Space direction="vertical" className="w-full">
          <div className="flex justify-between items-start">
            <Title level={3}>{listing.title}</Title>
            <Tag color={statusColors[listing.status]} className="text-base px-3 py-1">
              {listing.status}
            </Tag>
          </div>
          {listing.description && (
            <Text type="secondary">{listing.description}</Text>
          )}
        </Space>
      </Card>

      <Card title="Project Information">
        <Descriptions column={{ xs: 1, sm: 2 }}>
          <Descriptions.Item label="Project ID">{listing.id}</Descriptions.Item>
          <Descriptions.Item label="VIN">{listing.vin || 'N/A'}</Descriptions.Item>
          <Descriptions.Item label="Value Estimation">
            {listing.valueEstimation ? `${listing.valueEstimation} ETH` : 'Not estimated'}
          </Descriptions.Item>
          <Descriptions.Item label="Repair Estimation">
            {listing.repairEstimation ? `${listing.repairEstimation} ETH` : 'Not estimated'}
          </Descriptions.Item>
          <Descriptions.Item label="Created">
            {new Date(listing.createdAt).toLocaleDateString()}
          </Descriptions.Item>
          <Descriptions.Item label="Updated">
            {new Date(listing.updatedAt).toLocaleDateString()}
          </Descriptions.Item>
        </Descriptions>
      </Card>

      {listing.nftTokenId && (
        <NFTStatusCard listing={listing} />
      )}
    </Space>
  );
};

const NFTStatusCard: React.FC<{ listing: ProjectListing }> = ({ listing }) => (
  <Card title="NFT Status">
    <Descriptions column={{ xs: 1, sm: 2 }}>
      <Descriptions.Item label="Token ID">#{listing.nftTokenId}</Descriptions.Item>
      <Descriptions.Item label="Minted">
        <Tag color={listing.nftMinted ? 'green' : 'default'}>
          {listing.nftMinted ? 'Yes' : 'No'}
        </Tag>
      </Descriptions.Item>
      <Descriptions.Item label="Launched">
        <Tag color={listing.nftLaunched ? 'green' : 'default'}>
          {listing.nftLaunched ? 'Yes' : 'No'}
        </Tag>
      </Descriptions.Item>
      {listing.nftMetadataUri && (
        <Descriptions.Item label="Metadata URI">
          <Text copyable className="text-xs">{listing.nftMetadataUri}</Text>
        </Descriptions.Item>
      )}
    </Descriptions>
  </Card>
);
