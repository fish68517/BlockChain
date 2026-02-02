import { Card, Statistic, Tag, Typography, Space } from 'antd';
import type { ProjectListing } from '@/types';

const { Text } = Typography;

interface AuctionStatusProps {
  listing: ProjectListing;
}

export const AuctionStatus: React.FC<AuctionStatusProps> = ({ listing }) => {
  const isAuctionActive = listing.status === 'AUCTION';

  return (
    <Card title="Auction Status">
      <Space direction="vertical" className="w-full">
        <div className="flex justify-between items-center">
          <Text>Status</Text>
          <Tag color={isAuctionActive ? 'green' : 'default'}>
            {isAuctionActive ? 'Active' : listing.status}
          </Tag>
        </div>

        <Statistic
          title="Starting Price"
          value={listing.valueEstimation || 0}
          suffix="ETH"
          precision={4}
        />

        {listing.nftTokenId && (
          <div className="flex justify-between">
            <Text type="secondary">Token ID</Text>
            <Text>#{listing.nftTokenId}</Text>
          </div>
        )}
      </Space>
    </Card>
  );
};
