import { Table, Typography } from 'antd';
import type { Investment } from '@/types';

const { Text } = Typography;

interface InvestorListProps {
  investments: Investment[];
  loading?: boolean;
}

const columns = [
  {
    title: 'Investor',
    dataIndex: 'investorAddress',
    key: 'investorAddress',
    render: (addr: string) => (
      <Text copyable={{ text: addr }}>
        {addr.slice(0, 6)}...{addr.slice(-4)}
      </Text>
    ),
  },
  {
    title: 'Amount',
    dataIndex: 'amount',
    key: 'amount',
    render: (amount: number) => `${amount} ETH`,
  },
  {
    title: 'Date',
    dataIndex: 'createdAt',
    key: 'createdAt',
    render: (date: string) => new Date(date).toLocaleDateString(),
  },
];

export const InvestorList: React.FC<InvestorListProps> = ({
  investments,
  loading
}) => {
  return (
    <Table
      columns={columns}
      dataSource={investments}
      rowKey="id"
      loading={loading}
      pagination={{ pageSize: 5 }}
      locale={{ emptyText: 'No investments yet' }}
    />
  );
};
