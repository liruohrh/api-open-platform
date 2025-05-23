

import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { PageContainer, ProTable } from '@ant-design/pro-components';
import '@umijs/max';
import { Button, notification, Typography } from 'antd';
import React, { useRef, useState } from 'react';
import { changeSortValue, getSamePropButValueNotEquals, emptyValueToNull } from '@/utils';
import { PlusOutlined } from '@ant-design/icons';
import { addApi, listApi, updateApi, getApiById, rollOffApi, launchApi } from '@/services/api-platform/apiController';
import UpdateModalForm from './components/UpdateModalForm';
import ReadModal from './components/ReadModal';
import AddModalForm from './components/AddModalForm';

const AdminAPI: React.FC = () => {
    /**
     * @en-US The pop-up window of the distribution update window
     * @zh-CN 分布更新窗口的弹窗
     * */
    const [isOpenModal, setIsOpenModal] = useState<boolean>(false);
    const [isOpenAddModal, setIsOpenAddModal] = useState<boolean>(false);
    const [isOpenUpdateModal, setIsOpenUpdateModal] = useState<boolean>(false);
    const [current, setCurrent] = useState<number>(1);
    const actionRef = useRef<ActionType>();
    const [currentRow, setCurrentRow] = useState<API.HttpApiResp>();

    /**
     * @en-US International configuration
     * @zh-CN 国际化配置
     * */

    const columns: ProColumns<API.HttpApiResp>[] = [
        {
            title: 'ID',
            dataIndex: 'id',
            valueType: 'text',
        },
        {
            title: '名称',
            dataIndex: 'name',
            tooltip: "支持模糊搜索",
            render: (dom, entity) => {
                return (
                    <a
                        onClick={() => {
                            setIsOpenModal(true);
                            setCurrentRow(entity);
                        }}
                    >
                        {dom}
                    </a>
                );
            },
        },
        {
            title: 'logo',
            dataIndex: 'logoUrl',
            valueType: 'image'
        },
        {
            title: '描述',
            dataIndex: 'description',
            valueType: "text",
            tooltip: "支持模糊搜索",
        },

        {
            title: 'method',
            dataIndex: 'method',
            valueType: 'text',
        },
        {
            title: 'protocol',
            dataIndex: 'protocol',
            valueType: 'text',
        },
        {
            title: 'domain',
            dataIndex: 'domain',
            valueType: 'text',
        },
        {
            title: 'path',
            dataIndex: 'path',
            valueType: 'text',
        },
        {
            title: '价格',
            dataIndex: 'price',
            render: (dom, entity) => {
                if (entity.price === 0) {
                    return (
                        <Typography.Text>免费</Typography.Text>
                    );
                } else {
                    return (
                        <Typography.Text>💰 {entity.price}</Typography.Text>
                    );
                }
            },
        },
        {
            title: '免费次数',
            dataIndex: 'freeTimes',
            render: (dom, entity) => {
                if (entity.price === 0) {
                    return (
                        <Typography.Text>无限</Typography.Text>
                    );
                } else {
                    return (
                        <Typography.Text>{entity.freeTimes}</Typography.Text>
                    );
                }
            },
        },
        {
            title: '状态',
            dataIndex: 'status',
            valueEnum: {
                0: {
                    text: '待审核',
                    status: 'processing'
                },
                1: {
                    text: '上线',
                    status: 'success',
                },
                2: {
                    text: '下线',
                    status: "default",
                },
                3: {
                    text: '被禁用',
                    status: 'error',
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
            title: '更新时间',
            dataIndex: 'utime',
            valueType: 'dateTime',
            sorter: true,
        },
        {
            title: '操作',
            dataIndex: 'option',
            valueType: 'option',
            render: (_, entity, index, action) => [
                <a
                    key="update"
                    onClick={() => {
                        setIsOpenUpdateModal(true);
                        setCurrentRow(entity);

                    }}
                >
                    修改
                </a>,
                <a
                    key="上线"
                    onClick={async () => {
                        await launchApi({ apiId: entity.id!! });
                        action?.reload();
                    }}
                >
                    上线🚀
                </a>,
                <a
                    key="下线"
                    onClick={async () => {
                        await rollOffApi({ apiId: entity.id!! });
                        action?.reload();

                    }}
                >
                    下线
                </a>,
            ],
        },
    ];


    return (
        <PageContainer>
            <ProTable<API.HttpApiResp>
                pagination={{
                    // 必须有这2个
                    pageSize: 15,
                    current: current
                }}
                headerTitle={'API表格'}
                actionRef={actionRef}
                rowKey="id"
                cardBordered
                search={{
                    labelWidth: 120,
                }}
                //@ts-ignore
                request={async (params, sort, filter) => {

                    const current = params.current ?? 1;
                    const size = params.pageSize;
                    delete params.current;
                    delete params.pageSize;


                    try {
                        const data: API.RespPageRespHttpApiResp = await listApi({
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

                toolBarRender={() => [
                    <Button
                        type="primary"
                        key="primary"
                        onClick={() => {
                            setCurrentRow(undefined);
                            setIsOpenAddModal(true);
                        }}
                    >
                        <PlusOutlined /> 新建
                    </Button>,
                ]}
                columns={columns}
            />
            <AddModalForm
                handlerSubmit={async (values: API.HttpApiAddReq): Promise<void> => {
                    const data = await addApi({ ...values });
                    notification.success({ message: "添加成功！" });
                    setCurrentRow((await getApiById({ apiId: data.data!! })).data);
                    actionRef.current?.reload();
                    setIsOpenModal(true);
                    setIsOpenAddModal(false);

                }}
                handlerCloseModal={() => {
                    setIsOpenAddModal(false);
                }}
                isOpenModal={isOpenAddModal}
            />

            <ReadModal
                handlerCloseModal={() => {
                    setIsOpenModal(false);
                }}
                isOpenModal={isOpenModal}
                values={currentRow!!}
            />
            <UpdateModalForm
                handlerSubmit={async (values: API.HttpApiUpdateReq): Promise<void> => {
                    await updateApi({
                        ...getSamePropButValueNotEquals(values, currentRow),
                        id: values.id
                    });
                    setCurrentRow((await getApiById({ apiId: values.id })).data);
                    actionRef.current?.reload();
                    //更新完打开只读Modal
                    setIsOpenModal(true);
                    setIsOpenUpdateModal(false);
                }}
                handlerCloseModal={() => {
                    setIsOpenUpdateModal(false);
                }}
                isOpenModal={isOpenUpdateModal}
                values={currentRow!!}
            />
        </PageContainer>
    );
};
export default AdminAPI;
