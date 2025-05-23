import UpdateForm from '@/pages/Admin/User/components/UpdateForm';
import { listUser, getUserById } from '@/services/api-platform/userController';
import type { ActionType, ProColumns, ProDescriptionsItemProps } from '@ant-design/pro-components';
import { PageContainer, ProDescriptions, ProTable } from '@ant-design/pro-components';
import '@umijs/max';
import { Drawer } from 'antd';
import React, { useRef, useState } from 'react';
import { changeSortValue, emptyValueToNull, } from '@/utils';

const AdminUserPage: React.FC = () => {
  /**
   * @en-US The pop-up window of the distribution update window
   * @zh-CN 分布更新窗口的弹窗
   * */
  const [isOpenUpdateModal, setOpenUpdateModal] = useState<boolean>(false);
  const [showDetail, setShowDetail] = useState<boolean>(false);
  const [current, setCurrent] = useState<number>(1);
  const actionRef = useRef<ActionType>();
  const [currentRow, setCurrentRow] = useState<API.UserVo>();
  // const [selectedRowsState, setSelectedRows] = useState<API.UserVo[]>([]);


  /**
   * @en-US International configuration
   * @zh-CN 国际化配置
   * */

  const columns: ProColumns<API.UserVo>[] = [
    {
      title: 'ID',
      dataIndex: 'id',
      valueType: 'text',
    },
    {
      title: '昵称',
      dataIndex: 'nickname',
      valueType: 'text',
      tooltip: "支持模糊查询"
    },
    {
      title: '用户名',
      dataIndex: 'username',
      tooltip: "查看详细，支持模糊查询",
      render: (dom, entity) => {
        return (
          <a
            onClick={() => {
              setCurrentRow(entity);
              setShowDetail(true);
            }}
          >
            {dom}
          </a>
        );
      },

    },
    {
      title: '头像',
      dataIndex: 'avatarUrl',
      valueType: 'image',
      // render: (_, entity) => {
      //   return <Image src={entity.avatarUrl} width={50} height={50} />;
      // },
    },
    {
      title: '角色',
      dataIndex: 'role',
      valueEnum: {
        USER: {
          text: '普通用户',
          status: 'Default'
        },
        ADMIN: {
          text: '管理员',
          status: 'Success',
        },
      },
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      valueType: "text",
    },
    {
      title: '状态',
      dataIndex: 'status',
      valueEnum: {
        0: {
          text: '正常',
          status: 'Success',
        },
        1: {
          text: '冻结',
          status: 'Warning',
        },
      },
    },
    {
      title: '创建时间',
      dataIndex: 'ctime',
      valueType: 'dateTime',
      sorter: true,
    },
    {
      title: '个人描述',
      dataIndex: 'personalDescription',
      valueType: "text",
      tooltip: "支持模糊查询",
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => [
        <a
          key="config"
          onClick={() => {
            setOpenUpdateModal(true);
            setCurrentRow(record);
          }}
        >
          修改
        </a>,
      ],
    }
  ];

  return (
    <PageContainer>
      <ProTable<API.UserVo>
        pagination={{
          // 必须有这2个，虽然最终会根据响应的数组长度重新设置，但是pageSize不可以任意
          pageSize: 10,
          current: current
        }}
        headerTitle={'用户表格'}
        actionRef={actionRef}
        rowKey="id"
        cardBordered
        search={{
          labelWidth: 120,
        }}
        //@ts-ignore
        request={async (params, sort, filter) => {
          console.log(params, sort, filter);
          const current = params.current ?? 1;
          const size = params.pageSize;
          delete params.current;
          delete params.pageSize;


          try {
            const data: API.RespPageRespUserVo = await listUser({
              current,
              size,
              sort: changeSortValue(sort),
              //@ts-ignore
              search: emptyValueToNull(params)
            });
            setCurrent(current);

            return {
              data: data.data?.data,
              success: true,
              total: data.data?.total,
            };
          } catch (e) {
            return {
              success: false,
            };
          }
        }}
        columns={columns}
      // rowSelection={{
      //   onChange: (_, selectedRows) => {
      //     setSelectedRows(selectedRows);
      //   },
      // }}
      />
      <UpdateForm
        onFormFinish={async (submitSuccess: boolean): Promise<void> => {
          if (submitSuccess) {
            setOpenUpdateModal(false);
            // setShowDetail(false);
            setCurrentRow(undefined);
            actionRef.current?.getRealIndex
            if (actionRef.current) {
              await actionRef.current.reload();
            }
            getUserById({ userId: currentRow?.id!! }).then(resp => setCurrentRow(resp.data));
          }
        }}
        handlerCloseModal={() => {
          setOpenUpdateModal(false);
          if (!showDetail) {
            setCurrentRow(undefined);
          }
        }}
        isOpenUpdateModal={isOpenUpdateModal}
        values={currentRow || {}}
      />

      <Drawer
        width={600}
        open={showDetail}
        onClose={() => {
          setCurrentRow(undefined);
          setShowDetail(false);
        }}
        closable={false}
      >
        {currentRow?.id && (
          <ProDescriptions<API.UserVo>
            column={2}
            title={currentRow?.username}
            request={async () => ({
              data: currentRow || {},
            })}
            columns={[
              ...(columns as ProDescriptionsItemProps<API.UserVo>[]),
              {
                title: 'appKey',
                dataIndex: 'appKey',
                valueType: "text",
              }]}
          />
        )}
      </Drawer>
    </PageContainer>
  );
};
export default AdminUserPage;
